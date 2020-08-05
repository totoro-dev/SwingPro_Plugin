package top.totoro.plugin.test;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class RIDPsiImplUtil {

    public static String getKey(RIDProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(RIDTypes.KEY);
        if (keyNode != null) {
            // IMPORTANT: Convert embedded escaped spaces to simple spaces
            keyNode.getText().split("R\\.");
            return keyNode.getText().replaceAll("\\\\ ", " ");
        } else {
            return null;
        }
    }

    public static String getValue(RIDProperty element) {
        ASTNode valueNode = element.getNode().findChildByType(RIDTypes.VALUE);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }

    public static String getName(RIDProperty element) {
        return getKey(element);
    }

    public static PsiElement setName(RIDProperty element, String newName) {
        ASTNode keyNode = element.getNode().findChildByType(RIDTypes.KEY);
        if (keyNode != null) {
            RIDProperty property = RIDElementFactory.createProperty(element.getProject(), newName);
            ASTNode newKeyNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(RIDProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(RIDTypes.KEY);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }
}
