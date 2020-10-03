package top.totoro.swing.widget.exception;

import top.totoro.swing.widget.base.BaseAttribute;

public class AttributeException extends Exception {
    public AttributeException() {
        super();
    }

    public AttributeException(String message) {
        super(message);
    }

    public AttributeException(String resName, String nodeName, String nodeMsg, String attributeName, String attributeMsg) {
        super(resName + "文件中" + nodeName + "组件的" + nodeMsg + attributeName + "属性" + attributeMsg);
    }

    public static top.totoro.swing.widget.exception.AttributeException getAttrNameInvalid(BaseAttribute attribute, String attrName) {
        return new top.totoro.swing.widget.exception.AttributeException(new StringBuffer().append(attribute.getResName()).append("文件中").append(attribute.getNodeName()).append("组件的").append(attrName).append("属性无效。").toString());
    }

    public static top.totoro.swing.widget.exception.AttributeException getValueInvalid(BaseAttribute attribute, String attrName, String value, String msg) {
        return new top.totoro.swing.widget.exception.AttributeException(new StringBuffer().append(attribute.getResName()).append("文件中").append(attribute.getNodeName()).append("组件的").append(attrName).append("属性值: ").append(value).append(" ").append(msg).append("。").toString());
    }
}
