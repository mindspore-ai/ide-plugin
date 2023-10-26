import { markdownTableToJson } from "./readMD";
import { homedir } from 'os';
import { join } from 'path';
import { generateApiMappingUrl } from "../utils/constants";
import * as Constants from "../utils/constants";
import * as fs from "fs";
import { downloadFile } from "../utils/download";
import * as scanPlatform from "./scanPlatform";
import { window } from 'vscode';

let version = Constants.PYTORCH_API_MAPPING_DOWNLOAD_URL_VERSION;
let jsonData: Map<string, any[]> = new Map<string, any[]>();
let filePath = join(homedir(), ".mindspore", Constants.PYTORCH_API_MAPPING_FILENAME);
export async function init() {
    await initApiMap();
    window.showInformationMessage("API映射数据初始化成功");
}
export async function initApiMap() {
    let downloadFlag = await downloadFile(generateApiMappingUrl(version), Constants.PYTORCH_API_MAPPING_FILENAME, join(homedir(), ".mindspore"), 2000);
    let json: any[] | undefined;
    if (downloadFlag && fs.existsSync(filePath) && fs.statSync(filePath).size > 0) {
        json = markdownTableToJson(filePath);
    } else {
        json = markdownTableToJson("../pytorch_api_mapping.md");
    }
    if (json) {
        await scanPlatform.addPlatform(getJsonData());
        jsonData.set(version, json);
        return true;
    } else {
        return false;
    }
}

export async function addApiMap(newVersion: string) {
    let downloadFlag = await downloadFile(generateApiMappingUrl(newVersion), Constants.PYTORCH_API_MAPPING_FILENAME, join(homedir(), ".mindspore"), 2000);
    let json: any[] | undefined = undefined;
    if (downloadFlag && fs.existsSync(filePath) && fs.statSync(filePath).size > 0) {
        json = markdownTableToJson(filePath);
    }
    if (json) {
        await scanPlatform.addPlatform(json);
        jsonData.set(newVersion, json);

        return true;
    } else {
        return false;
    }
}

export function getJsonData() {
    return jsonData.get(version);
}

export async function changeVersion(newVersion: string) {
    let isInit = true;
    if (!jsonData.has(newVersion)) {
        isInit = await addApiMap(newVersion);
    }
    if (isInit) {
        version = newVersion;
        window.showInformationMessage("API映射数据版本切换成功");
    } else {
        window.showInformationMessage("API映射数据版本切换失败。请检查网络是否畅通或输入版本号是否存在。可以选择master版本获取最新的API映射数据!");
    }
    return isInit;
}

export function getVersion(){
    return version;
}