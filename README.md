# DataSource-Operation

数据源通用操作平台，采用**[spring-cloud-alibaba](https://github.com/alibaba/spring-cloud-alibaba)**分布式架构，基于**[ druid](https://github.com/alibaba/druid)**线程池和[Apache Commons Pool](https://commons.apache.org/pool/)实现高性能，高吞吐量的数据源管理服务。数据源的注册，销毁通过[RocketMQ](http://rocketmq.apache.org/)异步调用，提高系统响应速度。系统提供HTTP机GRPC通用API方便扩展。

# 支持数据源列表

- [MySQL](https://www.mysql.com/cn/)
- Oracle
- GuassDB
- [Apache Hive](https://hive.apache.org/)
- [Apache Kafka](https://kafka.apache.org/)
- [Apache HBase](https://hbase.apache.org/)
- [HDFS](https://hadoop.apache.org/docs/r1.2.1/hdfs_design.html)

华为FusionInsight：

- Hive
- Kafka
- HBase
- HDFS

