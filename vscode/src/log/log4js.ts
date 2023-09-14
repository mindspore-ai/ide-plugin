import { homedir } from "os";
import { join } from "path";
import { configure, getLogger } from "log4js";


let currentDate = new Date();
let year = currentDate.getFullYear().toString();
let month = (currentDate.getMonth() + 1).toString().padStart(2, "0");
let day = currentDate.getDate().toString();
var time = year.concat(month, day);
const logFileName = "whitzard-vscode".concat(time, ".log");

const config = {
    appenders: {
        whitzard:{type:"file", filename:join(homedir(), ".mindspore", "logs", logFileName)}
    },
    info: {
        type: 'dateFile',
        filename: 'logs/info',
        pattern: '-yyyy-MM-dd.log'
    },
    errorLog: {
        type: 'dateFile',
        filename: 'logs/error',
        pattern: '-yyyy-MM-dd.log'
    },
    categories: {
        default: {
            appenders: ['whitzard'],
            level: 'debug'
        },
        info: {
            appenders: ['whitzard'],
            level: 'info'
        },
        error: {
            appenders: ['whitzard'],
            level: 'error'
        },
    }
};

configure(config);
export var logger = getLogger("default");

