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

package com.mindspore.ide.toolkit.demo;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.Presentation;

/**
 * utils
 *
 * @since 2022-04-18
 */
public class DemoSearchTypeActionUtils {
    /**
     * utils
     *
     * @param type type
     */
    public static void demoSearchTypeActionUtils(int type) {
        Presentation presentation1 = null;
        if (ActionManager.getInstance().getAction("UserType.NEWBIE") instanceof DemoSearchType1Action) {
            DemoSearchType1Action demoSearchType1Action =
                    (DemoSearchType1Action) ActionManager.getInstance().getAction("UserType.NEWBIE");
            presentation1 = demoSearchType1Action.getTemplatePresentation();
        }
        Presentation presentation2 = null;
        if (ActionManager.getInstance().getAction("UserType.TRANSFER") instanceof DemoSearchType2Action) {
            DemoSearchType2Action demoSearchType2Action =
                    (DemoSearchType2Action) ActionManager.getInstance().getAction("UserType.TRANSFER");
            presentation2 = demoSearchType2Action.getTemplatePresentation();
        }
        Presentation presentation3 = null;
        if (ActionManager.getInstance().getAction("UserType.MASTER") instanceof DemoSearchType3Action) {
            DemoSearchType3Action demoSearchType3Action =
                    (DemoSearchType3Action) ActionManager.getInstance().getAction("UserType.MASTER");
            presentation3 = demoSearchType3Action.getTemplatePresentation();
        }
        demoSearchTypeActionUtils(type, presentation1, presentation2, presentation3);
    }

    private static void demoSearchTypeActionUtils(int type, Presentation presentation1,
            Presentation presentation2, Presentation presentation3) {
        switch (type) {
            case 1:
                UserType.setCurrent(UserType.NEWBIE);
                if (presentation1 != null) {
                    presentation1.setText("（√） 新手");
                }
                if (presentation2 != null) {
                    presentation2.setText("    其他框架使用者");
                }
                if (presentation3 != null) {
                    presentation3.setText("    MindSpore大师");
                }
                break;
            case 2:
                UserType.setCurrent(UserType.TRANSFER);
                if (presentation1 != null) {
                    presentation1.setText("    新手");
                }
                if (presentation2 != null) {
                    presentation2.setText("（√） 其他框架使用者");
                }
                if (presentation3 != null) {
                    presentation3.setText("    MindSpore大师");
                }
                break;
            case 3:
                UserType.setCurrent(UserType.MASTER);
                if (presentation1 != null) {
                    presentation1.setText("    新手");
                }
                if (presentation2 != null) {
                    presentation2.setText("    其他框架使用者");
                }
                if (presentation3 != null) {
                    presentation3.setText("（√） MindSpore大师");
                }
                break;
            default:
                break;
        }
    }
}