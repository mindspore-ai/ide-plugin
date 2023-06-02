import * as vscode from 'vscode';
import { getResult } from './readMD';

interface SearchResult {
	path: string;
	line: string;
	content: string;
  }


export class OpenWebProvider implements vscode.WebviewViewProvider{
	public static readonly viewType = 'exampleView';
	
	private _view?: vscode.WebviewView;

	constructor(
		private readonly _extensionUri: vscode.Uri,
	){ }

	public resolveWebviewView(
		webviewView: vscode.WebviewView, 
		context: vscode.WebviewViewResolveContext, 
		_token: vscode.CancellationToken,
	){
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
					result.line = highlightText(result.line, message.keyword)
				})
				webviewView.webview.postMessage({ type: 'searchResult', results, keyword:message.keyword});
				break;
			  case 'openURL':
				// vscode.workspace.openTextDocument(message.path).then((doc) => {
				//   vscode.window.showTextDocument(doc, { selection: new vscode.Range(message.line - 1, 0, message.line - 1, 0) });
				// });
				const pagePanel = vscode.window.createWebviewPanel(
					'pageView',
					'Web Page',
					vscode.ViewColumn.Two,
					{
					  enableScripts: true
					}
				  );
				  pagePanel.webview.html = `
				  <html>
					<head>
					  <style>
						html, body {
						  height: 100%;
						  margin: 0;
						  padding: 0;
						}
						#container {
						  height: 100%;
						}
						#page {
						  width: 100%;
						  height: 100%;
						  border: none;
						}
					  </style>
					</head>
					<body>
					  <div id="container">
						<iframe id="page" src="${message.path}"></iframe>
					  </div>
					</body>
				  </html>
				`;
				break;
			}
		  });
	
	}

	private _getHtmlForWebview(webview: vscode.Webview){

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
			  color: #000000;
			}
			.search-results li {
			  margin-bottom: 5px;
			  cursor: pointer;
			  background-color: #f5f5f5;
			  padding: 5px;
			}
			.search-results li:hover {
			  background-color: #e0e0e0;
			}
			.keyword-match{
			  color:red;
			}
			.highlight {
				background-color: yellow;
			}
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
		  //   searchInput.addEventListener('keydown', (event) => {
		  // 	if (event.key === 'Enter') {
		  // 	  const keyword = searchInput.value;
		  // 	  vscode.postMessage({ type: 'search', keyword });
		  // 	}
		  //   });
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
				  li.innerHTML = \`\${result.content} - \${result.line} - \${result.path}\`;
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

function search(keyword: string): Array<SearchResult> {
	const results: Array<SearchResult> = [];
	const resultJson = getResult("D:/MindSporeToolkit/ide-plugin/vscode/test.md",[keyword])
	resultJson?.forEach(resultOneJson =>
		{
			results.push({
				path:resultOneJson.operatorURL,
				line: resultOneJson.mindspore1word,
				content:resultOneJson.operator1word
			})
		})

	return results;
  }


function highlightText(text: string, keyword: string): string {
	const regex = new RegExp(`(${keyword})`, 'i');
	return text.replace(regex, '<span class="highlight">$1</span>');
}
