// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: dataset.proto

package com.jhr.datasource.operation.api.grpc.dataset;

/**
 * Protobuf enum {@code dataset.DataSourceMfrs}
 */
public enum DataSourceMfrs
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>DSM_NOT_SPECIFIED = 0;</code>
   */
  DSM_NOT_SPECIFIED(0),
  /**
   * <code>NL = 1;</code>
   */
  NL(1),
  /**
   * <code>HW = 7;</code>
   */
  HW(7),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>DSM_NOT_SPECIFIED = 0;</code>
   */
  public static final int DSM_NOT_SPECIFIED_VALUE = 0;
  /**
   * <code>NL = 1;</code>
   */
  public static final int NL_VALUE = 1;
  /**
   * <code>HW = 7;</code>
   */
  public static final int HW_VALUE = 7;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static DataSourceMfrs valueOf(int value) {
    return forNumber(value);
  }

  public static DataSourceMfrs forNumber(int value) {
    switch (value) {
      case 0: return DSM_NOT_SPECIFIED;
      case 1: return NL;
      case 7: return HW;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<DataSourceMfrs>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      DataSourceMfrs> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<DataSourceMfrs>() {
          public DataSourceMfrs findValueByNumber(int number) {
            return DataSourceMfrs.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return com.jhr.datasource.operation.api.grpc.dataset.DataSetProto.getDescriptor().getEnumTypes().get(4);
  }

  private static final DataSourceMfrs[] VALUES = values();

  public static DataSourceMfrs valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private DataSourceMfrs(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:dataset.DataSourceMfrs)
}

