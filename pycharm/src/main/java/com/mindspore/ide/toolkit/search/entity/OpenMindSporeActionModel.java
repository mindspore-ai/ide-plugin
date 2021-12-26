package com.mindspore.ide.toolkit.search.entity;

import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenMindSporeActionModel {
    private Project myProject;

    private DocumentValue docValue;

    public OpenMindSporeActionModel(Project myProject, DocumentValue docValue) {
        this.myProject = myProject;
        this.docValue = docValue;
    }

    @Override
    public String toString() {
        return "OpenMindSporeActionModel{" +
                "myProject=" + myProject +
                ", docValue=" + docValue +
                '}';
    }
}
