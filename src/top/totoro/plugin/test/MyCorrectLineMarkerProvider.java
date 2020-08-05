package top.totoro.plugin.test;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public class MyCorrectLineMarkerProvider implements LineMarkerProvider {
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof PsiIdentifier && element.getParent() instanceof PsiMethod)
            return new LineMarkerInfo(element, null, SimpleIcons.FILE, null, null, GutterIconRenderer.Alignment.CENTER);
        return null;
    }
}