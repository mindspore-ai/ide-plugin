package com.mindspore.ide.toolkit.common.utils;

import org.junit.Assert;
import org.junit.Test;

public class GsonUtilsTest {

    @Test
    public void testGsonUtils(){
        Assert.assertNotNull(GsonUtils.INSTANCE.getGson());
    }
}
