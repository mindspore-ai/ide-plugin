import * as vscode from 'vscode';
import {getResult} from './readMD';


export interface SearchResult {
    path: string;
    line: string;
    content: string;
}


export class OpenWebProvider implements vscode.WebviewViewProvider {
    public static readonly viewType = 'MindSporeSearchApiView';

    private _view?: vscode.WebviewView;

    constructor(
        private readonly _extensionUri: vscode.Uri,
    ) {
    }

    public resolveWebviewView(
        webviewView: vscode.WebviewView,
        context: vscode.WebviewViewResolveContext,
        _token: vscode.CancellationToken,
    ) {
        this._view = webviewView;

        webviewView.webview.options = {
            enableScripts: true,

            localResourceRoots: [
                this._extensionUri
            ]
        };

        webviewView.webview.html = this._getHtmlForWebview(webviewView.webview);


        webviewView.webview.onDidReceiveMessage((message) => {
            switch (message.type) {
                case 'search':
                    const results = search(message.keyword);
                    results.forEach((result) => {
                        result.content = highlightText(result.content, message.keyword);
                        result.line = highlightText(result.line, message.keyword);
                    });
                    webviewView.webview.postMessage({type: 'searchResult', results, keyword: message.keyword});
                    break;
                case 'openURL':
                    vscode.env.openExternal(vscode.Uri.parse(message.path));
                    break;
            }
        });

    }

    private _getHtmlForWebview(webview: vscode.Webview) {

        return `
		<html>
		<head>
		  <style>
			.search-box {
			  margin: 10px;
			}
			.search-box input {
			  width: 100%;
			  padding: 5px;
			  font-size: 16px;
			}
			.search-results {
			  margin: 10px;
			  list-style: none;
			  padding: 0;
			  //color: #ffffff;
			}
			.search-results li {
			  margin-bottom: 0px;
			  cursor: pointer;
			  //background-color: #000000;
			  padding: 0px;
			}
			.search-results li:hover {
			  background-color: #e0e0e0;
			}
			.keyword-match{
			  color:red;
			}
			// .highlight {
			// 	background-color: orange;
			// }
		  </style>
		</head>
		<body>
		  <div class="search-box">
			<input type="text" placeholder="Enter keyword" id="searchInput">
		  </div>
		  <ul class="search-results" id="searchResults"></ul>
		  <script>
			const vscode = acquireVsCodeApi();
			const searchInput = document.getElementById('searchInput');
			const searchResults = document.getElementById('searchResults');
			console.log('Webview is loaded');
			console.log(searchInput);
			searchInput.addEventListener('input', () => {
			  const keyword = searchInput.value;
			  vscode.postMessage({ type: 'search', keyword });
			});
  
			window.addEventListener('message', (event) => {
			  if (event.data.type === 'searchResult') {
				searchResults.innerHTML = '';
				event.data.results.forEach((result) => {
				  const li = document.createElement('li');
				  //li.textContent = \`\${result.content} - \${result.line} - \${result.path}\`;
				  li.innerHTML = \`\${result.content} -> \${result.line}\`;
				  li.addEventListener('click', () => {
					vscode.postMessage({ type: 'openURL', path: result.path });
				  });
				  searchResults.appendChild(li);
				});
			  }
			});
		  </script>
		</body>
		</html>
	  `;
    }
}

export function search(keyword: string): Array<SearchResult> {
    const results: Array<SearchResult> = [];
    const resultJson = getResult("../pytorch_api_mapping.md", [keyword]);

    resultJson?.forEach(resultOneJson => {
        results.push({
            path: resultOneJson.mindsporeURL,
            line: resultOneJson.mindspore1word,
            content: resultOneJson.operator1word
        });
    });

    return results;
}


function highlightText(text: string, keyword: string): string {
    const regex = new RegExp(`(${keyword})`, 'i');
    return text.replace(regex, '<span class="highlight">$1</span>');
}
