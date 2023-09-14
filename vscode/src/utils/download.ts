import * as fs from "fs";
import * as path from "path";
import axios from 'axios';
import * as compressing from "compressing";
import { window } from "vscode";
import { logger } from "../log/log4js";
import { fsExistsSync } from "./fileUtil";
import * as fsPromises from "fs/promises";

export async function downloadFile(url: string, fileName: string, destination: string, timeout: number){
	if (!fsExistsSync(destination)){
		fs.mkdirSync(destination);
	}
    let filePath = path.join(destination, fileName);
    const writer = fs.createWriteStream(filePath);

    try{
        const response = await axios({
            url,
            method: 'GET',
            responseType: 'stream',
            timeout: timeout
        }).then(responses => {
            responses.data.on('close', () => {
                throw new Error("network interrupt!");
            });
            return responses;
        });
        
        response.data.pipe(writer);

        await new Promise((resolve, reject) => {
            writer.on('finish', resolve);
            writer.on('error', reject);
            response.data.on('error', reject);
        });
        return true;
    } catch (error) {
        writer.close();
        logger.warn(error);
        await fsPromises.rm(filePath);
        return false;
    }
}

export async function unzipSync(fileName: string, destination: string) {
    return compressing.zip.uncompress(path.join(destination, fileName),destination).then((res:any)=> {
        logger.info('decompression successful');
    }).catch((err: any) =>{
        logger.error('decompression failed');
        throw err;
    });
}

export async function download(fileName: string, url: string, destination: string) {
    await downloadFile(url, fileName, destination, 1800000);
    const fineNameArr = fileName.split('.');
    const suffix = fineNameArr[fineNameArr.length - 1];
    switch (suffix) {
        case 'zip':
            await unzipSync(fileName, destination);
            fs.unlinkSync(path.join(destination, fileName));
            break;
        case 'json':
            break;
        default:
            logger.error(`suffix of file to download is incorrect, file name is ${fileName}`);
            window.showInformationMessage(`处理下载的文件失败,文件名:${fileName}`);
    }
    
}