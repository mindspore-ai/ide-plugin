package com.mindspore.ide.toolkit.common.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyFromImportStatement;
import com.jetbrains.python.psi.PyImportElement;

/**
 * xxx
 *
 * @since 2022-12-20
 */
public class MSPsiUtils {
    /**
     * 判断psi是否是import
     *
     * @param element psi element
     * @return true or false
     */
    public static boolean isPsiImport(PsiElement element) {
        if (element == null) {
            return false;
        }
        PyImportElement pyImportElement = PsiTreeUtil.getParentOfType(element, PyImportElement.class);
        PyFromImportStatement pyFromImportStatement = PsiTreeUtil.getParentOfType(element, PyFromImportStatement.class);
        return pyImportElement != null || pyFromImportStatement != null;
    }
}
