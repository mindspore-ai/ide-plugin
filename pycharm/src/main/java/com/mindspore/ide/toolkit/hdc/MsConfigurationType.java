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

package com.mindspore.ide.toolkit.hdc;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import icons.MsIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * MsConfigurationType
 *
 * @since 2022-04-18
 */
public class MsConfigurationType implements ConfigurationType {
    @Override
    @NotNull
    @Nls(capitalization = Nls.Capitalization.Title)
    public String getDisplayName() {
        return "MindSporeWebInteractress";
    }

    @Override
    @Nls(capitalization = Nls.Capitalization.Sentence)
    public String getConfigurationTypeDescription() {
        return "To communicate with web";
    }

    @Override
    public Icon getIcon() {
        return MsIcons.MS_ICON_16PX;
    }

    @Override
    @NotNull
    @NonNls
    public String getId() {
        return "MsConfiguration";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new MsConfigurationFactory(this)};
    }
}