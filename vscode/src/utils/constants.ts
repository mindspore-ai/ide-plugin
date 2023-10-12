import { userInfo } from "os";
import * as vscode from "vscode";
import { logger } from "../log/log4js";
import fs = require('fs');
import path = require("path");
import { window } from "vscode";
import yaml = require('js-yaml');


export const X86_PYTHON_PORT = 50053;
export const LINUX_PYTHON_PORT = 50055;
export const PYTORCH_API_MAPPING_DOWNLOAD_URL_PREFIX = "https://gitee.com/mindspore/docs/raw/";
export const PYTORCH_API_MAPPING_DOWNLOAD_URL_VERSION = "2.1";
export const PYTORCH_API_MAPPING_DOWNLOAD_URL_SUFFIX = "/docs/mindspore/source_zh_cn/note/api_mapping/pytorch_api_mapping.md";
export const PYTORCH_API_MAPPING_FILENAME = "pytorch_api_mapping.md";

export function generateApiMappingUrl(version?: string) {
    return PYTORCH_API_MAPPING_DOWNLOAD_URL_PREFIX + (version === "master"?"":"r") + (version ?? PYTORCH_API_MAPPING_DOWNLOAD_URL_VERSION) + PYTORCH_API_MAPPING_DOWNLOAD_URL_SUFFIX;
}

export enum OsInfo{
    windows = "windows",
    linuxX86 = "Linux x86_64",
    linuxAArch = "Linux aarch64",
    linux = "linux",
    mac = "mac"
}

let systemKernel = "windows";
if (process.platform === "win32"){
    systemKernel = OsInfo.windows;
} else if (process.platform === "linux"){
    systemKernel = OsInfo.linux;
} else if (process.platform === "darwin"){
    systemKernel = OsInfo.mac;
}
export let getSystemKernel = () => systemKernel;


let uid: string;

export async function getUid():Promise<string> {
    if (uid) {
        return uid;
    }
    uid = userInfo().username;
    if (process.platform === 'linux') {
        let user = await vscode.commands.executeCommand<any>('vscode.getUserInfo');
        if (user){
            uid = user.name;
        }
    }
    logger.info("mindspore plugin current user:" + uid);
    return uid;

}

export async function getVersion() {
    var result = await vscode.commands.executeCommand("getContext") as string;
    return result;
}

export async function getDownloadInfo(platform:string) {
    let versionNumber = await getVersion();
    var configFileContents: any;
    let yamlPath = path.join(__dirname, '..', 'complete.yaml');
    try {
        configFileContents = fs.readFileSync(yamlPath, "utf8");
    } catch (error: any) {
        if (error.code ==='ENOENT') {
            logger.error("access config file error msg:" + JSON.stringify(error).slice(0,500));
            window.showInformationMessage("未找到配置文件，请先下载配置文件");
            throw error;
        } else {
            throw error;
        }
    }
    let result = yaml.load(configFileContents);
    const jsonString = JSON.stringify(result);
    const configYaml = JSON.parse(jsonString);

    if (!configYaml.modelMap.hasOwnProperty(versionNumber)){
        versionNumber = "default_plugin_version";
    };

    let modelURL = configYaml.modelMap[versionNumber][platform]?.modelDownloadUrl??"";
    let modelZIP = configYaml.modelMap[versionNumber][platform]?.modelZipName??"";
    let modelDIR = configYaml.modelMap[versionNumber][platform]?.modelUnzipFolderName??"";
    let modelPATH = configYaml.modelMap[versionNumber][platform]?.modelExePath??"";
    return {modelURL, modelZIP, modelDIR, modelPATH};

}