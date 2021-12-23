package com.mindspore.ide.toolkit.common.events;

import com.mindspore.ide.toolkit.common.config.GuideUserEntity;
import lombok.Data;

public class GuideUserEvents {

    @Data
    public static class DontAskAgain{
        private GuideUserEntity entity;

        public DontAskAgain(GuideUserEntity entity) {
            this.entity = entity;
        }
    }
}
