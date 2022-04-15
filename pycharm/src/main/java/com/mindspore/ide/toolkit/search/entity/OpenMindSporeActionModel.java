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

package com.mindspore.ide.toolkit.search.entity;

import com.intellij.openapi.project.Project;

public class OpenMindSporeActionModel {
    private Project myProject;
    private DocumentValue docValue;

    public OpenMindSporeActionModel(Project myProject, DocumentValue docValue) {
        this.myProject = myProject;
        this.docValue = docValue;
    }

    public Project getMyProject() {
        return myProject;
    }

    public void setMyProject(Project myProject) {
        this.myProject = myProject;
    }

    public DocumentValue getDocValue() {
        return docValue;
    }

    public void setDocValue(DocumentValue docValue) {
        this.docValue = docValue;
    }
}