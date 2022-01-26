package com.mindspore.ide.toolkit.smartcomplete;

import com.mindspore.ide.toolkit.common.events.ProjectEvents;
import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

@Listener(references = References.Strong)
public class SmartCompleteListener {
    @Handler(delivery = Invoke.Asynchronously)
    public void ideStart(ProjectEvents.AppFrameCreated init) {
        ModelManager.INSTANCE.startCompleteModel();
    }

    @Handler(delivery = Invoke.Asynchronously)
    public void codeComplete(SmartCompleteEvents.CodeRecommendStart codeRecommendStart) {
        CompleteService.COMPLETE.onPredicting(codeRecommendStart);
    }

    @Handler(delivery = Invoke.Asynchronously)
    public void appWillBeClosed(ProjectEvents.ProjectClosed events) {
        ModelManager.INSTANCE.shutDownSmartCompleteModel();
    }
}