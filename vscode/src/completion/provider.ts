import { credentials } from '@grpc/grpc-js';
import { promisify } from 'util';
import * as vscode from 'vscode';
import { GreeterClient } from '../grpc/recommendation_grpc_pb';
import { CompleteReply, CompleteRequest } from '../grpc/recommendation_pb';
import { logger } from '../log/log4js';
import { getTransformerXLPort } from '../utils/portUtils';
import { getTransformerXLServer, TransformerXLServer } from './server';

const MAX_LENGTH = 300;
export const ADDITIONAL_CHARACTERS = [".", "[", "{", "<", ">", "(", "]", "=", "+", "-", "_"];

export class WhitzardProvider implements vscode.CompletionItemProvider{
    private static instance: WhitzardProvider;
    private client?: GreeterClient;
    private transformerXLServer?: TransformerXLServer;

    private constructor(){
    }

    public static async getInstance(){
        if (!WhitzardProvider.instance){
            WhitzardProvider.instance = new WhitzardProvider();
            await WhitzardProvider.instance.init();
        }
        return WhitzardProvider.instance;
    }

    private static getCompleteRequest(document: vscode.TextDocument, position: vscode.Position){
        const offset = document.offsetAt(position);

        const beforePosition = document.positionAt(offset - MAX_LENGTH);
        const beforeRange = new vscode.Range(beforePosition, position);
        const before = document.getText(beforeRange);

        const afterPosition = document.positionAt(offset + MAX_LENGTH);
        const afterRange = new vscode.Range(position, afterPosition);
        const after = document.getText(afterRange);

        const line = document.lineAt(position.line);
        let text = line.text;
        const start = line.firstNonWhitespaceCharacterIndex;
        const end = position.character;
        text = text.substring(start, end);
        const prefix = text.split(/\W/).pop() ?? '';

        let request = new CompleteRequest();
        request.setBefore(before);
        request.setAfter(after);
        request.setPrefix(prefix);

        return request;
    }

    public async provideCompletionItems(document: vscode.TextDocument, position: vscode.Position, token: vscode.CancellationToken, context: vscode.CompletionContext){
        const request= WhitzardProvider.getCompleteRequest(document, position);
        logger.debug("recommend request data:", JSON.stringify(request));
        if (!this.client){
            logger.info("WhitzardProvider instance has not been initialized");
            throw new Error("WhitzardProvider instance has not been initialized");
        }
        const getRecommendation = promisify(this.client.getRecommendation).bind(this.client);
        const reply = await getRecommendation(request) as CompleteReply;
        const resultList = reply.getResults()?.getResultEntryList();
        logger.debug("mindspore model response data:", JSON.stringify(resultList));
        let completionList: vscode.CompletionItem[] = [];
        const uniqPrefix = new Set();

        resultList?.forEach((result: { getNewPrefix: () => string; getDetails: () => any; }) => {
            const newPrefix = result.getNewPrefix().trimEnd();
            const isRedundant = uniqPrefix.has(newPrefix) || request.getBefore().endsWith(newPrefix);

            if (!isRedundant) {
                uniqPrefix.add(newPrefix);

                const item = new vscode.CompletionItem(newPrefix);

                item.sortText = String(1 - Number(result.getDetails()));
                item.detail = "MindSpore Dev Toolkit";
                item.command = {
                    "command" : "mindspore.completion-selected",
                    "title" : "mindspore Completion Selected",
                    "arguments" : [item]
                };
                item.kind = vscode.CompletionItemKind.Snippet;
                if (ADDITIONAL_CHARACTERS.some(() => context.triggerCharacter)){
                    if (newPrefix.startsWith(reply.getOldPrefix())){
                        item.insertText = newPrefix.slice(reply.getOldPrefix().length,);
                    }
                }
                completionList.push(item);
            }
        });
        
        logger.debug("recommend response data:", JSON.stringify(completionList));
        return completionList;
    }
    public async deactivate(){
        this.client?.close();
        return this.transformerXLServer?.kill();
    }

    private async init() {
        let port = await getTransformerXLPort();
        if (!port){
            logger.info("No vacant prot !");
            throw new Error("No vacant prot !");
        }
        logger.info(`TransformerXLPort is :${port}`);
        this.transformerXLServer = await getTransformerXLServer(port);
        this.client = new GreeterClient(`localhost:${port}`, credentials.createInsecure());
                
    }
}