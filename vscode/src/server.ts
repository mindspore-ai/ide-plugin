import { ChildProcess, execFile } from "child_process";
import { homedir } from "os";
import { join } from "path";
import * as fsPromises from "fs/promises"
import { window } from "vscode";
import { download } from "./download";
import { logger } from "./log/log4js";
import { Constants, getDownloadInfo, getVersion, system_kernel } from "./constants";

export async function getTransformerXLServer(port: number) {
    let modelInfo = await getDownloadInfo(system_kernel)
    return await TransformerXLServer.getInstance(modelInfo.modelURL, modelInfo.modelZIP, modelInfo.modelDIR, modelInfo.modelPATH, port);
}

export class TransformerXLServer{
    private static instance: TransformerXLServer;
    public childProcess?: ChildProcess;
    private readonly url: string;
    private readonly root: string;
    private readonly workDir: string;
    private readonly modelPath: string;
    private readonly zip: string;
    private readonly port: number;

    private constructor(url: string, zip: string, dir: string, path: string[], port: number){
        this.url = url;
        this.root = join(homedir(), ".mindspore");
        this.workDir = join(this.root, dir);
        this.modelPath = join(this.workDir, path.toString());
        this.zip = zip;
        this.port = port;

    }

    public static async getInstance(url: string, zip: string, dir: string, path: string[], port: number) {
        if (!TransformerXLServer.instance) {
            TransformerXLServer.instance = new TransformerXLServer(url, zip, dir, path, port);
            await TransformerXLServer.instance.init();
        }
        return TransformerXLServer.instance;
    }

    public async kill() {
        this.childProcess?.kill();
    }

    private async init() {
        if (this.childProcess){
            return;
        }
        try {
            await fsPromises.access(this.modelPath);
        } catch {
            window.showInformationMessage("开始下载 Model");
            logger.info("python_completion download start");
            try{
                await download(this.zip, this.url, this.workDir);
            } catch (error) {
                logger.error("python_completion download error msg:" + JSON.stringify(error).slice(0,500));
                window.showInformationMessage("下载 Model 失败");
                throw error;
            }
            logger.info("python_completion download success");
            window.showInformationMessage("下载 Model 成功")
        }

        if (process.platform == 'linux'){
            await fsPromises.stat(this.modelPath).then(res =>{
                if (res.mode !== 0o777){
                    fsPromises.chmod(this.modelPath, 0o777);
                }

            });
        }
        this.childProcess = execFile(this.modelPath, ["-p", this.port.toString()], {cwd: this.workDir});
        this.childProcess.stderr?.on("data", (data) =>{
            logger.warn(data);
        })
    }
}