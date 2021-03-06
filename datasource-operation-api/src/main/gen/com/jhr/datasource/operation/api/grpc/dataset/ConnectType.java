// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: dataset.proto

package com.jhr.datasource.operation.api.grpc.dataset;

/**
 * Protobuf enum {@code dataset.ConnectType}
 */
public enum ConnectType
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>CT_NOT_SPECIFIED = 0;</code>
   */
  CT_NOT_SPECIFIED(0),
  /**
   * <code>SID = 1;</code>
   */
  SID(1),
  /**
   * <code>SERVICE_NAME = 2;</code>
   */
  SERVICE_NAME(2),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>CT_NOT_SPECIFIED = 0;</code>
   */
  public static final int CT_NOT_SPECIFIED_VALUE = 0;
  /**
   * <code>SID = 1;</code>
   */
  public static final int SID_VALUE = 1;
  /**
   * <code>SERVICE_NAME = 2;</code>
   */
  public static final int SERVICE_NAME_VALUE = 2;


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
  public static ConnectType valueOf(int value) {
    return forNumber(value);
  }

  public static ConnectType forNumber(int value) {
    switch (value) {
      case 0: return CT_NOT_SPECIFIED;
      case 1: return SID;
      case 2: return SERVICE_NAME;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<ConnectType>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      ConnectType> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<ConnectType>() {
          public ConnectType findValueByNumber(int number) {
            return ConnectType.forNumber(number);
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
    return com.jhr.datasource.operation.api.grpc.dataset.DataSetProto.getDescriptor().getEnumTypes().get(5);
  }

  private static final ConnectType[] VALUES = values();

  public static ConnectType valueOf(
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

  private ConnectType(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:dataset.ConnectType)
}

