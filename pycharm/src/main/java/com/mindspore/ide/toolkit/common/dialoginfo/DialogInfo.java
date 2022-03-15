/*
 * Copyright 2021-2022 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mindspore.ide.toolkit.common.dialoginfo;

/**
 * 弹窗中需要展示的信息。
 * 该类作为所有类型弹窗展示信息的父类，充当策略模式的对外超类。
 * 在需要调出弹窗的时候，只要调用{@link DialogInfo#showDialog}方法即可
 *
 * @since 1.0
 */
public abstract class DialogInfo {
    private boolean isSuccessful;

    private String title;

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * show dialog
     */
    public abstract void showDialog();

    public void showDialog(String title) {
        this.title = title;
        showDialog();
    }
}