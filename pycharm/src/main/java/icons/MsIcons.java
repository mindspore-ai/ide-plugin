package icons;

import com.intellij.openapi.util.IconLoader;
import com.mindspore.ide.toolkit.common.beans.NormalInfoConstants;

import javax.swing.*;

public interface MsIcons {
    Icon MS_ICON_12PX = IconLoader.getIcon("/icons/12px.svg", NormalInfoConstants.class);
    Icon MS_ICON_13PX = IconLoader.getIcon("/icons/13px.svg", NormalInfoConstants.class);
    Icon MS_ICON_16PX = IconLoader.getIcon("/icons/16px.svg", NormalInfoConstants.class);
    Icon MS_ICON_16PXWP = IconLoader.getIcon("/icons/MindSporeWithPadding.svg", NormalInfoConstants.class);
}