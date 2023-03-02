// package: proto
// file: src/grpc/recommendation.proto

import * as jspb from "google-protobuf";

export class CompleteRequest extends jspb.Message {
  getPrefix(): string;
  setPrefix(value: string): void;

  getBefore(): string;
  setBefore(value: string): void;

  getAfter(): string;
  setAfter(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CompleteRequest.AsObject;
  static toObject(includeInstance: boolean, msg: CompleteRequest): CompleteRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: CompleteRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CompleteRequest;
  static deserializeBinaryFromReader(message: CompleteRequest, reader: jspb.BinaryReader): CompleteRequest;
}

export namespace CompleteRequest {
  export type AsObject = {
    prefix: string,
    before: string,
    after: string,
  }
}

export class CompleteReply extends jspb.Message {
  getOldPrefix(): string;
  setOldPrefix(value: string): void;

  hasResults(): boolean;
  clearResults(): void;
  getResults(): ResultEntries | undefined;
  setResults(value?: ResultEntries): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CompleteReply.AsObject;
  static toObject(includeInstance: boolean, msg: CompleteReply): CompleteReply.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: CompleteReply, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CompleteReply;
  static deserializeBinaryFromReader(message: CompleteReply, reader: jspb.BinaryReader): CompleteReply;
}

export namespace CompleteReply {
  export type AsObject = {
    oldPrefix: string,
    results?: ResultEntries.AsObject,
  }
}

export class ResultEntries extends jspb.Message {
  clearResultentryList(): void;
  getResultentryList(): Array<ResultEntry>;
  setResultentryList(value: Array<ResultEntry>): void;
  addResultentry(value?: ResultEntry, index?: number): ResultEntry;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ResultEntries.AsObject;
  static toObject(includeInstance: boolean, msg: ResultEntries): ResultEntries.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ResultEntries, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ResultEntries;
  static deserializeBinaryFromReader(message: ResultEntries, reader: jspb.BinaryReader): ResultEntries;
}

export namespace ResultEntries {
  export type AsObject = {
    resultentryList: Array<ResultEntry.AsObject>,
  }
}

export class ResultEntry extends jspb.Message {
  getNewPrefix(): string;
  setNewPrefix(value: string): void;

  getDetails(): string;
  setDetails(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ResultEntry.AsObject;
  static toObject(includeInstance: boolean, msg: ResultEntry): ResultEntry.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ResultEntry, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ResultEntry;
  static deserializeBinaryFromReader(message: ResultEntry, reader: jspb.BinaryReader): ResultEntry;
}

export namespace ResultEntry {
  export type AsObject = {
    newPrefix: string,
    details: string,
  }
}

