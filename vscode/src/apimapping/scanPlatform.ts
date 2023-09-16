import axios from "axios";
import { logger } from '../log/log4js';

const platform: string[] = ['Ascend', 'GPU', 'CPU'];
let platformMap: Map<string, string> = new Map();

export async function addPlatform(pyJsonData: any[] | undefined) {
    if (pyJsonData === null || pyJsonData === undefined) {
        return;
    }
    for (let i = 0; i < 5; i++) {
        let dataPromiseList: Promise<void>[] = [];
        for (let data of pyJsonData) {
            let apiName = data.mindspore1word;
            if (platformMap.has((apiName))) {
                continue;
            }
            let promise = new Promise<void>(resolve => {
                axios({
                    url: data.mindsporeURL,
                    method: 'get'
                }).then((response: any) => {
                    let htmlText = response.data as string;
                    //apiName位置
                    let apiNameIndex = htmlText.indexOf('<dt class="sig sig-object py" id="' + apiName);
                    let platformIndex = htmlText.indexOf('支持平台', apiNameIndex);
                    if (!htmlText.substring(apiNameIndex, platformIndex).includes('class="py ')) {
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
                })
            })
            dataPromiseList.push(promise);
        }
        Promise.allSettled(dataPromiseList);
    }
}

export async function addSinglePlatform(data: any) {
    for (let i = 0; i < 5; i++) {
        let apiName = data.mindspore1word;
        if (platformMap.has((apiName))) {
            return ;
        }
        return new Promise<void>(resolve => {
            axios({
                url: data.mindsporeURL,
                method: 'get'
            }).then((response: any) => {
                let htmlText = response.data as string;
                //apiName位置
                let apiNameIndex = htmlText.indexOf('<dt class="sig sig-object py" id="' + apiName);
                let platformIndex = htmlText.indexOf('支持平台', apiNameIndex);
                if (!htmlText.substring(apiNameIndex, platformIndex).includes('class="py ')) {
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
            })
        })
    }
}