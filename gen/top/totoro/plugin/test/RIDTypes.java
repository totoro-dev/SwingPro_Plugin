// This is a generated file. Not intended for manual editing.
package top.totoro.plugin.test;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import top.totoro.plugin.test.impl.*;

public interface RIDTypes {

  IElementType PROPERTY = new RIDElementType("PROPERTY");

  IElementType COMMENT = new RIDTokenType("COMMENT");
  IElementType CRLF = new RIDTokenType("CRLF");
  IElementType KEY = new RIDTokenType("KEY");
  IElementType SEPARATOR = new RIDTokenType("SEPARATOR");
  IElementType VALUE = new RIDTokenType("VALUE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == PROPERTY) {
        return new RIDPropertyImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
