package com.jhr.datasource.operation.api.grpc.dataset;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.23.0)",
    comments = "Source: dataset.proto")
public final class DataSetServiceGrpc {

  private DataSetServiceGrpc() {}

  public static final String SERVICE_NAME = "dataset.DataSetService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo,
      com.jhr.datasource.operation.api.grpc.response.ResponseResult> getQueryForListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QueryForList",
      requestType = com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo.class,
      responseType = com.jhr.datasource.operation.api.grpc.response.ResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo,
      com.jhr.datasource.operation.api.grpc.response.ResponseResult> getQueryForListMethod() {
    io.grpc.MethodDescriptor<com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo, com.jhr.datasource.operation.api.grpc.response.ResponseResult> getQueryForListMethod;
    if ((getQueryForListMethod = DataSetServiceGrpc.getQueryForListMethod) == null) {
      synchronized (DataSetServiceGrpc.class) {
        if ((getQueryForListMethod = DataSetServiceGrpc.getQueryForListMethod) == null) {
          DataSetServiceGrpc.getQueryForListMethod = getQueryForListMethod =
              io.grpc.MethodDescriptor.<com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo, com.jhr.datasource.operation.api.grpc.response.ResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryForList"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.jhr.datasource.operation.api.grpc.response.ResponseResult.getDefaultInstance()))
              .setSchemaDescriptor(new DataSetServiceMethodDescriptorSupplier("QueryForList"))
              .build();
        }
      }
    }
    return getQueryForListMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DataSetServiceStub newStub(io.grpc.Channel channel) {
    return new DataSetServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DataSetServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DataSetServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DataSetServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DataSetServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class DataSetServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void queryForList(com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo request,
        io.grpc.stub.StreamObserver<com.jhr.datasource.operation.api.grpc.response.ResponseResult> responseObserver) {
      asyncUnimplementedUnaryCall(getQueryForListMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getQueryForListMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo,
                com.jhr.datasource.operation.api.grpc.response.ResponseResult>(
                  this, METHODID_QUERY_FOR_LIST)))
          .build();
    }
  }

  /**
   */
  public static final class DataSetServiceStub extends io.grpc.stub.AbstractStub<DataSetServiceStub> {
    private DataSetServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DataSetServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataSetServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DataSetServiceStub(channel, callOptions);
    }

    /**
     */
    public void queryForList(com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo request,
        io.grpc.stub.StreamObserver<com.jhr.datasource.operation.api.grpc.response.ResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getQueryForListMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DataSetServiceBlockingStub extends io.grpc.stub.AbstractStub<DataSetServiceBlockingStub> {
    private DataSetServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DataSetServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataSetServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DataSetServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.jhr.datasource.operation.api.grpc.response.ResponseResult queryForList(com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo request) {
      return blockingUnaryCall(
          getChannel(), getQueryForListMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DataSetServiceFutureStub extends io.grpc.stub.AbstractStub<DataSetServiceFutureStub> {
    private DataSetServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DataSetServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataSetServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DataSetServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.jhr.datasource.operation.api.grpc.response.ResponseResult> queryForList(
        com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo request) {
      return futureUnaryCall(
          getChannel().newCall(getQueryForListMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_QUERY_FOR_LIST = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DataSetServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DataSetServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_QUERY_FOR_LIST:
          serviceImpl.queryForList((com.jhr.datasource.operation.api.grpc.dataset.ConnectionInfo) request,
              (io.grpc.stub.StreamObserver<com.jhr.datasource.operation.api.grpc.response.ResponseResult>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class DataSetServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DataSetServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.jhr.datasource.operation.api.grpc.dataset.DataSetProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DataSetService");
    }
  }

  private static final class DataSetServiceFileDescriptorSupplier
      extends DataSetServiceBaseDescriptorSupplier {
    DataSetServiceFileDescriptorSupplier() {}
  }

  private static final class DataSetServiceMethodDescriptorSupplier
      extends DataSetServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DataSetServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (DataSetServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DataSetServiceFileDescriptorSupplier())
              .addMethod(getQueryForListMethod())
              .build();
        }
      }
    }
    return result;
  }
}
