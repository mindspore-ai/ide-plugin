import { userInfo } from "os";
import * as vscode from "vscode";
import { logger } from "./log/log4js";
import fs = require('fs');
import path = require("path");
import { window } from "vscode";
import yaml = require('js-yaml');


export class Constants{
    public static readonly X86_PYTHON_PORT = 50053;
    public static readonly LINUX_PYTHON_PORT = 50055;
    public static readonly PYTORCH_API_MAPPING_DOWNLOAD_URL = "https://gitee.com/mindspore/docs/raw/r2.1/docs/mindspore/source_zh_cn/note/api_mapping/pytorch_api_mapping.md";
    public static readonly PYTORCH_API_MAPPING_FILENAME = "pytorch_api_mapping.md";
}

export enum osInfo{
    WINDOWS = "windows",
    LINUX_X86 = "Linux x86_64",
    LINUX_AARCH = "Linux aarch64",
    LINUX = "linux",
    MAC = "mac"
}

export let system_kernel = "windows";
if (process.platform === "win32"){
    system_kernel = osInfo.WINDOWS;
} else if (process.platform === "linux"){
    system_kernel = osInfo.LINUX;
} else if (process.platform === "darwin"){
    system_kernel = osInfo.MAC;
}

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
    const jsonstr = JSON.stringify(result);
    const configYmal = JSON.parse(jsonstr);

    if (!configYmal.modelMap.hasOwnProperty(versionNumber)){
        versionNumber = "default_plugin_version";
    };

    let modelURL = configYmal.modelMap[versionNumber][platform]?.modelDownloadUrl??"";
    let modelZIP = configYmal.modelMap[versionNumber][platform]?.modelZipName??"";
    let modelDIR = configYmal.modelMap[versionNumber][platform]?.modelUnzipFolderName??"";
    let modelPATH = configYmal.modelMap[versionNumber][platform]?.modelExePath??"";
    return {modelURL, modelZIP, modelDIR, modelPATH};

}