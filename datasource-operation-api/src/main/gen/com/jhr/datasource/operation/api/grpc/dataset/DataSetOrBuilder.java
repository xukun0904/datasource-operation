// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: dataset.proto

package com.jhr.datasource.operation.api.grpc.dataset;

public interface DataSetOrBuilder extends
    // @@protoc_insertion_point(interface_extends:dataset.DataSet)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .dataset.RowData dataset = 1;</code>
   */
  java.util.List<com.jhr.datasource.operation.api.grpc.dataset.RowData> 
      getDatasetList();
  /**
   * <code>repeated .dataset.RowData dataset = 1;</code>
   */
  com.jhr.datasource.operation.api.grpc.dataset.RowData getDataset(int index);
  /**
   * <code>repeated .dataset.RowData dataset = 1;</code>
   */
  int getDatasetCount();
  /**
   * <code>repeated .dataset.RowData dataset = 1;</code>
   */
  java.util.List<? extends com.jhr.datasource.operation.api.grpc.dataset.RowDataOrBuilder> 
      getDatasetOrBuilderList();
  /**
   * <code>repeated .dataset.RowData dataset = 1;</code>
   */
  com.jhr.datasource.operation.api.grpc.dataset.RowDataOrBuilder getDatasetOrBuilder(
      int index);

  /**
   * <code>int64 total = 2;</code>
   */
  long getTotal();
}
