// This is a generated file. Not intended for manual editing.
package top.totoro.plugin.core.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import top.totoro.plugin.core.SimpleNamedElementImpl;
import top.totoro.plugin.core.*;

public class SimplePropertyImpl extends SimpleNamedElementImpl implements SimpleProperty {

  public SimplePropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SimpleVisitor visitor) {
    visitor.visitProperty(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SimpleVisitor) accept((SimpleVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public String getKey() {
    return SimplePsiImplUtil.getKey(this);
  }

  @Override
  public String getValue() {
    return SimplePsiImplUtil.getValue(this);
  }

  @Override
  public String getName() {
    return SimplePsiImplUtil.getName(this);
  }

  @Override
  public PsiElement setName(String newName) {
    return SimplePsiImplUtil.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return SimplePsiImplUtil.getNameIdentifier(this);
  }

}
