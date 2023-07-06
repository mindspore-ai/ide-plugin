import { markdownTableToJson } from "./readMD";
import { markdownTable } from 'markdown-table';
import { tensorApi } from "./resource/specialTorch";
import { homedir } from 'os';
import { join } from 'path';
import { Constants } from "./constants";
import * as fs from "fs";

let jsonData: any[] | null;
let filePath = join(homedir(), ".mindspore", Constants.PYTORCH_API_MAPPING_FILENAME);
export function initApiMap(downloadFlag: boolean) {
    if (downloadFlag && fs.existsSync(filePath) && fs.statSync(filePath).size > 0) {
        jsonData = markdownTableToJson(filePath);
    } else {
        jsonData = markdownTableToJson("../pytorch_api_mapping.md");
    }
}

export function scanAPI(apis: string[]){
    const head = ["PyTorch API","API 版本", "MindSpore API", "说明"];
    const headInconvertible = ["PyTorch API", "说明"];
    let convertible = new Map<string, string[]>();
    let inconvertible = new Map<string, string[]>();
    let chainCall = new Map<string, string[]>();
    let chainCallInconvertible = new Map<string, string[]>();


    let tempMap = new Map<string, any>();
    jsonData?.forEach((row) => {
        tempMap.set(row.operator1word, row);
    });

    apis.forEach((rawApi) => {
        let api = rawApi.replace(/([^\w.])/g, "");
        if (tempMap.has(api)) {
            let target = tempMap.get(api);
            let record = [
                target.operatorURL?`[${target.operator1word}](${target.operatorURL})`:target.operator1word,
                target.version,
                target.mindsporeURL?`[${target.mindspore1word}](${target.mindsporeURL})`:target.mindspore1word,
                target.diffURL?`[${target.remark}](${target.diffURL})`: target.remark];
            convertible.set(api,record);
        } else {
            if (api.startsWith("torch")) {
                inconvertible.set(api, [api,""]);
            } else {
                let apiName = api.split(".").pop()??"";
                if (tensorApi.indexOf(apiName) > 0) {
                    apiName = "torch.Tensor." + apiName;
                    let target = tempMap.get(apiName);
                    let record = [apiName, "可能为torch.Tensor的API"];
                    if (target) {
                        record = [
                            target.operatorURL?`[${target.operator1word}](${target.operatorURL})`:target.operator1word,
                            target.version,
                            target.mindsporeURL?`[${target.mindspore1word}](${target.mindsporeURL})`:target.mindspore1word,
                            target.diffURL?`[${target.remark}](${target.diffURL})`: target.remark];
                            chainCall.set(apiName,record);
                    } else {
                        chainCallInconvertible.set(apiName,record);
                    }
                }
            }
        }

    });
    // let table = markdownTable([{
    //     operatorURL: "https://pytorch.org/docs/1.8.1/generated/torch.argsort.html",
    //     operator1word: "torch.argsort",
    //     mindsporeURL: "https://www.mindspore.cn/docs/zh-CN/r2.0/api_python/ops/mindspore.ops.argsort.html",
    //     mindspore1word: "mindspore.ops.argsort",
    //     diffURL: "",
    //   }
    let convertibleTable = [];
    if (convertible.size > 0) {
        convertibleTable.push(head);
        (new Map([...convertible].sort())).forEach((value) => {
            convertibleTable.push(value);
        });
    } else {
        convertibleTable.push(["无"]);
    }
    let inconvertibleTable = [];
    if (inconvertible.size > 0) {
        inconvertibleTable.push(headInconvertible);
        (new Map([...inconvertible].sort())).forEach((value) => {
            inconvertibleTable.push(value);
        });
    } else {
        inconvertibleTable.push(["无"]);
    }
    let callChainTable = [];
    if (chainCall.size > 0 || chainCallInconvertible.size > 0) {
        callChainTable.push(head);
        (new Map([...chainCall].sort())).forEach((value) => {
            callChainTable.push(value);
        });
        (new Map([...chainCallInconvertible].sort())).forEach((value) => {
            inconvertibleTable.push(value);
        });
    } else {
        callChainTable.push(["无"]);
    }
    const table = [markdownTable(convertibleTable),markdownTable(callChainTable),markdownTable(inconvertibleTable)];

    return table;
}