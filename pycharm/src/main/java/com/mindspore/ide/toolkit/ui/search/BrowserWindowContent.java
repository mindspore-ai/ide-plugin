package com.mindspore.ide.toolkit.ui.search;

import com.intellij.openapi.Disposable;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;

public class BrowserWindowContent implements Disposable {
    private final JPanel content;

    private JBCefBrowser jbCefBrowser;

    private String url;


    public BrowserWindowContent(String url) {
        this.url = url;

        content = new JPanel(new BorderLayout());
        if(!JBCefApp.isSupported()){
            content.add(new JLabel("ide don't support JCEF",SwingConstants.CENTER));

        }else {
            jbCefBrowser = new JBCefBrowser();
            content.add(jbCefBrowser.getComponent(),BorderLayout.CENTER);
            jbCefBrowser.loadURL(url);
        }
    }

    @Override
    public String toString(){
        return "BrowserWindowContent{ content = "+content+"}";
    }

    public JPanel getContent(){
        return content;
    }

    public void refreshBrowser(){
        jbCefBrowser.loadURL(url);
    }

    public void loadUrl(String url){
        jbCefBrowser.loadURL(url);
    }

    public CefBrowser getCefBrowser(){
        return jbCefBrowser.getCefBrowser();
    }

    @Override
    public void dispose() {

    }
}
