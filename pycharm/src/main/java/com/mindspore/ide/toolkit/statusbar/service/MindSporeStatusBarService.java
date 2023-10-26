package com.mindspore.ide.toolkit.statusbar.service;

import com.intellij.util.messages.Topic;

public interface MindSporeStatusBarService {
    Topic<MindSporeStatusBarService> TOPIC = Topic.create("mindSpore.status.bar", MindSporeStatusBarService.class);

    void mindSporeVersionChanged(String version);
}
