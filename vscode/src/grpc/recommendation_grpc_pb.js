// GENERATED CODE -- DO NOT EDIT!

'use strict';
var grpc = require('@grpc/grpc-js');
var src_grpc_recommendation_pb = require('../../src/grpc/recommendation_pb.js');

function serialize_proto_CompleteReply(arg) {
  if (!(arg instanceof src_grpc_recommendation_pb.CompleteReply)) {
    throw new Error('Expected argument of type proto.CompleteReply');
  }
  return Buffer.from(arg.serializeBinary());
}

function deserialize_proto_CompleteReply(buffer_arg) {
  return src_grpc_recommendation_pb.CompleteReply.deserializeBinary(new Uint8Array(buffer_arg));
}

function serialize_proto_CompleteRequest(arg) {
  if (!(arg instanceof src_grpc_recommendation_pb.CompleteRequest)) {
    throw new Error('Expected argument of type proto.CompleteRequest');
  }
  return Buffer.from(arg.serializeBinary());
}

function deserialize_proto_CompleteRequest(buffer_arg) {
  return src_grpc_recommendation_pb.CompleteRequest.deserializeBinary(new Uint8Array(buffer_arg));
}


// The greeting service definition.
var GreeterService = exports.GreeterService = {
  // Sends a greeting
getRecommendation: {
    path: '/proto.Greeter/getRecommendation',
    requestStream: false,
    responseStream: false,
    requestType: src_grpc_recommendation_pb.CompleteRequest,
    responseType: src_grpc_recommendation_pb.CompleteReply,
    requestSerialize: serialize_proto_CompleteRequest,
    requestDeserialize: deserialize_proto_CompleteRequest,
    responseSerialize: serialize_proto_CompleteReply,
    responseDeserialize: deserialize_proto_CompleteReply,
  },
};

exports.GreeterClient = grpc.makeGenericClientConstructor(GreeterService);
