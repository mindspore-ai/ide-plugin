package com.mindspore.ide.toolkit;

import com.intellij.ide.AppLifecycleListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Initializer implements AppLifecycleListener {

    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        AppLifecycleListener.super.appFrameCreated(commandLineArgs);
    }

    @Override
    public void appClosing() {
        AppLifecycleListener.super.appClosing();
    }

    @Override
    public void appWillBeClosed(boolean isRestart) {
        AppLifecycleListener.super.appWillBeClosed(isRestart);
    }
}
