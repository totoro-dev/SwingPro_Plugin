// This is a generated file. Not intended for manual editing.
package top.totoro.plugin.test;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class RIDVisitor extends PsiElementVisitor {

  public void visitProperty(@NotNull RIDProperty o) {
    visitNamedElement(o);
  }

  public void visitNamedElement(@NotNull RIDNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
