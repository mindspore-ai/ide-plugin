import axios from "axios";
import { logger } from '../log/log4js';

const platform: string[] = ['Ascend', 'GPU', 'CPU'];


export async function addPlatform(pyJsonData: any[] | undefined) {
    if (pyJsonData === null || pyJsonData === undefined) {
        return;
    }
    let platformMap: Map<string, string> = new Map();
    for (let i = 0; i < 5; i++) {
        let dataPromiseList: Promise<void>[] = [];
        for (let data of pyJsonData) {
            let apiName = data.mindspore1word;
            if (platformMap.has((apiName))) {
                continue;
            }
            if (data.mindsporeURL === undefined || data.mindsporeURL === null || data.mindsporeURL === "") {
                data.platform = '--'; 
            }
            let promise = new Promise<void>(resolve => {
                axios({
                    url: data.mindsporeURL,
                    method: 'get',
                    timeout: 30000
                }).then((response: any) => {
                    let htmlText = response.data as string;
                    //apiName位置
                    let apiNameIndex = htmlText.indexOf('<dt class="sig sig-object py" id="' + apiName);
                    let platformIndex = htmlText.indexOf('支持平台', apiNameIndex);
                    if (apiNameIndex > 0 && platformIndex > 0 && !htmlText.substring(apiNameIndex, platformIndex).includes('class="py ')) {
                        let platformString = htmlText.substring(platformIndex, htmlText.indexOf('</dd>', platformIndex));
                        let platformList: string[] = [];
                        platform.forEach(type => {
                            if (platformString.includes(type)) {
                                platformList.push(type);
                            }
                        });

                        if (platformList.length > 0) {
                            data.platform = platformList.toString();
                        }
                    } else {
                        data.platform = '暂无数据';
                    }
                    platformMap.set(apiName, data.platform);
                    resolve();
                }).catch((error: any) => {
                    logger.warn(`get ${apiName} html content msg: ${error}`);
                    resolve();
                });
            });
            dataPromiseList.push(promise);
        }
        await Promise.allSettled(dataPromiseList);
    }
}

export async function addSinglePlatform(data: any) {
    for (let i = 0; i < 5; i++) {
        let apiName = data.mindspore1word;
        if (data.platform !== "") {
            return ;
        }
        if (data.mindsporeURL === undefined || data.mindsporeURL === null || data.mindsporeURL === "") {
            data.platform = '--'; 
        }
        return new Promise<void>(resolve => {
            axios({
                url: data.mindsporeURL,
                method: 'get',
                timeout: 3000
            }).then((response: any) => {
                let htmlText = response.data as string;
                //apiName位置
                let apiNameIndex = htmlText.indexOf('<dt class="sig sig-object py" id="' + apiName);
                let platformIndex = htmlText.indexOf('支持平台', apiNameIndex);
                if (apiNameIndex > 0 && platformIndex > 0 && !htmlText.substring(apiNameIndex, platformIndex).includes('class="py ')) {
                    let platformString = htmlText.substring(platformIndex, htmlText.indexOf('</dd>', platformIndex));
                    let platformList: string[] = [];
                    platform.forEach(type => {
                        if (platformString.includes(type)) {
                            platformList.push(type);
                        }
                    });

                    if (platformList.length > 0) {
                        data.platform = platformList.toString();
                    }
                } else {
                    data.platform = '暂无数据';
                }
                resolve();
            }).catch((error: any) => {
                logger.warn(`get ${apiName} html content msg: ${error}`);
                resolve();
            })
        })
    }
}