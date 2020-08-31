package top.totoro.plugin.core;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class RIDNamedElementImpl  extends ASTWrapperPsiElement implements RIDNamedElement {
    public RIDNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}