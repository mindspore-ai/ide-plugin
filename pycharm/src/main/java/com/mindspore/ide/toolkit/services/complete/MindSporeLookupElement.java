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

package com.mindspore.ide.toolkit.services.complete;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;

import icons.MsIcons;

import org.jetbrains.annotations.NotNull;

/**
 * MindSpore LookupElement
 *
 * @since 2022-02-16
 */
public class MindSporeLookupElement extends LookupElement {
    private LookupElement lookupElement;

    private DataType dataType = DataType.ONLY_IN_MINDSPORE;

    /**
     * MindSporeLookupElement Constructor
     *
     * @param lookupElement lookup element
     */
    public MindSporeLookupElement(LookupElement lookupElement) {
        this.lookupElement = lookupElement;
    }

    /**
     * MindSporeLookupElement Constructor
     *
     * @param lookupElement lookup element
     * @param dataType data type
     */
    public MindSporeLookupElement(LookupElement lookupElement, DataType dataType) {
        this(lookupElement);
        this.dataType = dataType;
    }

    public LookupElement getLookupElement() {
        return lookupElement;
    }

    public DataType getDataType() {
        return dataType;
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context) {
        lookupElement.handleInsert(context);
    }

    @NotNull
    @Override
    public String getLookupString() {
        return lookupElement.getLookupString();
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        lookupElement.renderElement(presentation);
        presentation.setIcon(MsIcons.MS_ICON_16PX);
    }

    /**
     * 数据类型
     */
    public enum DataType {
        /**
         * 数据只存在于mindspore
         */
        ONLY_IN_MINDSPORE,

        /**
         * 数据存在mindspore和jetbrains里
         */
        BOTH_IN_MINDSPORE_AND_JETBRAINS
    }
}
