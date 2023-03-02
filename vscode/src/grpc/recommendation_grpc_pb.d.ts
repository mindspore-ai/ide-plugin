// GENERATED CODE -- DO NOT EDIT!

// package: proto
// file: src/grpc/recommendation.proto

import * as src_grpc_recommendation_pb from "../../src/grpc/recommendation_pb";
import * as grpc from "@grpc/grpc-js";

interface IGreeterService extends grpc.ServiceDefinition<grpc.UntypedServiceImplementation> {
  getRecommendation: grpc.MethodDefinition<src_grpc_recommendation_pb.CompleteRequest, src_grpc_recommendation_pb.CompleteReply>;
}

export const GreeterService: IGreeterService;

export interface IGreeterServer extends grpc.UntypedServiceImplementation {
  getRecommendation: grpc.handleUnaryCall<src_grpc_recommendation_pb.CompleteRequest, src_grpc_recommendation_pb.CompleteReply>;
}

export class GreeterClient extends grpc.Client {
  constructor(address: string, credentials: grpc.ChannelCredentials, options?: object);
  getRecommendation(argument: src_grpc_recommendation_pb.CompleteRequest, callback: grpc.requestCallback<src_grpc_recommendation_pb.CompleteReply>): grpc.ClientUnaryCall;
  getRecommendation(argument: src_grpc_recommendation_pb.CompleteRequest, metadataOrOptions: grpc.Metadata | grpc.CallOptions | null, callback: grpc.requestCallback<src_grpc_recommendation_pb.CompleteReply>): grpc.ClientUnaryCall;
  getRecommendation(argument: src_grpc_recommendation_pb.CompleteRequest, metadata: grpc.Metadata | null, options: grpc.CallOptions | null, callback: grpc.requestCallback<src_grpc_recommendation_pb.CompleteReply>): grpc.ClientUnaryCall;
}
