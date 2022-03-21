package com.jhr.datasource.operation.common.service.grpc;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jhr.datasource.operation.api.domain.dto.DatasourceConnectionInfo;
import com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo;
import com.jhr.datasource.operation.api.grpc.dataset.DataSet;
import com.jhr.datasource.operation.api.grpc.dataset.DataSetServiceGrpc;
import com.jhr.datasource.operation.api.grpc.dataset.RowData;
import com.jhr.datasource.operation.api.grpc.response.ResponseResult;
import com.jhr.datasource.operation.common.component.summoner.DatasourceSummoner;
import com.jhr.datasource.operation.common.component.summoner.DatasourceSummonerStrategy;
import com.jhr.datasource.operation.common.domain.constant.Constants;
import com.jhr.datasource.operation.common.util.DatasourceOperationUtils;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xukun
 * @since 1.0
 */
@GrpcService
public class DataSetService extends DataSetServiceGrpc.DataSetServiceImplBase {

    @Autowired
    private DatasourceSummonerStrategy strategy;

    @Override
    public void queryForList(ConnectionInfo request, StreamObserver<ResponseResult> responseObserver) {
        // request转换成DatasourceConnectionInfo
        DatasourceConnectionInfo connectionInfo = DatasourceOperationUtils.getDatasourceConnectionInfo(request);
        DatasourceSummoner summoner = strategy.getSummonerByBeanName(Constants.DATASOURCE_BEAN_NAME_MAP.get(connectionInfo.getDsourceType()));
        Map<String, Object[]> countSqlMap = new HashMap<>();
        Map<String, Object[]> querySqlMap = new HashMap<>();
        // 根据类型生成SqlMap
        obtainMapByType(countSqlMap, querySqlMap, connectionInfo.getSqlMap());
        // 查询总条数
        Long total = 0L;
        if (CollectionUtil.isNotEmpty(countSqlMap)) {
            connectionInfo.setSqlMap(countSqlMap);
            total = summoner.getTotal(connectionInfo);
        }
        // 执行SQL
        List<Map<String, Object>> dataList = Collections.emptyList();
        if (CollectionUtil.isNotEmpty(querySqlMap)) {
            connectionInfo.setSqlMap(querySqlMap);
            dataList = summoner.queryForList(connectionInfo);
        }
        // 封装返回值
        DataSet dataSet = getDataSet(dataList, total);
        responseObserver.onNext(DatasourceOperationUtils.success(dataSet));
        responseObserver.onCompleted();
    }

    private void obtainMapByType(Map<String, Object[]> countSqlMap, Map<String, Object[]> querySqlMap, Map<String, Object[]> sqlMap) {
        for (Map.Entry<String, Object[]> sqlEntry : sqlMap.entrySet()) {
            JSONObject jsonObject = JSONUtil.parseObj(sqlEntry.getKey());
            String key = jsonObject.getStr("type");
            String sql = jsonObject.getStr(key);
            if ("count_sql".equals(key)) {
                countSqlMap.put(sql, sqlEntry.getValue());
            } else if ("sql".equals(key)) {
                querySqlMap.put(sql, sqlEntry.getValue());
            }
        }
    }

    private DataSet getDataSet(List<Map<String, Object>> dataList, Long total) {
        DataSet.Builder datasetBuilder = DataSet.newBuilder();
        datasetBuilder.setTotal(total);
        for (Map<String, Object> originData : dataList) {
            Map<String, String> targetData = new HashMap<>();
            for (Map.Entry<String, Object> entry : originData.entrySet()) {
                String value = DatasourceOperationUtils.parseString(entry.getValue());
                targetData.put(entry.getKey(), value);
            }
            RowData rowData = RowData.newBuilder().putAllRowData(targetData).build();
            datasetBuilder.addDataset(rowData);
        }
        return datasetBuilder.build();
    }
}
