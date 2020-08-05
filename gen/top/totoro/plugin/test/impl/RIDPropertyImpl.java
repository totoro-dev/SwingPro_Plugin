// This is a generated file. Not intended for manual editing.
package top.totoro.plugin.test.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static top.totoro.plugin.test.RIDTypes.*;
import top.totoro.plugin.test.RIDNamedElementImpl;
import top.totoro.plugin.test.*;

public class RIDPropertyImpl extends RIDNamedElementImpl implements RIDProperty {

  public RIDPropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RIDVisitor visitor) {
    visitor.visitProperty(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RIDVisitor) accept((RIDVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public String getKey() {
    return RIDPsiImplUtil.getKey(this);
  }

  @Override
  public String getValue() {
    return RIDPsiImplUtil.getValue(this);
  }

  @Override
  public String getName() {
    return RIDPsiImplUtil.getName(this);
  }

  @Override
  public PsiElement setName(String newName) {
    return RIDPsiImplUtil.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return RIDPsiImplUtil.getNameIdentifier(this);
  }

}
