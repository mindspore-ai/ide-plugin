package com.mindspore.ide.toolkit.common.events;

import net.engio.mbassy.bus.MBassador;

public enum EventCenter {
    INSTANCE;

    private final MBassador<Object> bus = new MBassador<>();

    public void publish(Object event){
        bus.publishAsync(event);
    }

    public void subscribe(Object listener){
        bus.subscribe(listener);
    }
}
