import { markdownTable } from 'markdown-table';
import { tensorApi } from "../resource/specialTorch";
import * as apiMappingDataSource from "./apiMappingData";

export function mapAPI(apis: string[]){
    const head = ["PyTorch API","API 版本", "MindSpore API", "说明", "支持的后端"];
    const headInconvertible = ["PyTorch API", "说明"];
    let convertible = new Map<string, string[]>();
    let inconvertible = new Map<string, string[]>();
    let chainCall = new Map<string, string[]>();
    let chainCallInconvertible = new Map<string, string[]>();


    let tempMap = new Map<string, any>();
    apiMappingDataSource.getJsonData()?.forEach((row) => {
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
                target.diffURL?`[${target.remark}](${target.diffURL})`: target.remark,
                target.platform];
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
                            target.diffURL?`[${target.remark}](${target.diffURL})`: target.remark,
                            target.platform],
                            chainCall.set(apiName,record);
                    } else {
                        chainCallInconvertible.set(apiName,record);
                    }
                }
            }
        }

    });
    
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
    if (inconvertible.size > 0 || chainCallInconvertible.size > 0) {
        inconvertibleTable.push(headInconvertible);
        (new Map([...inconvertible].sort())).forEach((value) => {
            inconvertibleTable.push(value);
        });
        (new Map([...chainCallInconvertible].sort())).forEach((value) => {
            inconvertibleTable.push(value);
        });
    } else {
        inconvertibleTable.push(["无"]);
    }
    let callChainTable = [];
    if (chainCall.size > 0) {
        callChainTable.push(head);
        (new Map([...chainCall].sort())).forEach((value) => {
            callChainTable.push(value);
        });
    } else {
        callChainTable.push(["无"]);
    }
    const table = [markdownTable(convertibleTable),markdownTable(callChainTable),markdownTable(inconvertibleTable)];

    return table;
}