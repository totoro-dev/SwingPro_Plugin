package top.totoro.plugin.core;
// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.totoro.plugin.util.Log;
import top.totoro.swing.widget.util.AttributeKey;

import java.util.*;

import static top.totoro.plugin.constant.AttributeDefaultValue.*;

public class SimpleCompletionContributor extends CompletionContributor {

    private static final String TAG = SimpleCompletionContributor.class.getSimpleName();

    public static List<LookupElement> customTagLookElements = new LinkedList<>();
    static List<LookupElement> tagLookupElements = new LinkedList<>();
    static List<LookupElement> keyLookupElements = new LinkedList<>();
    static List<LookupElement> valueLookupElements = new LinkedList<>();

    public static List<String> layoutTags;
    public static List<String> viewTags;

    /* 标签对应的构建字段 */
    static Map<String, List<LookupElement>> tagKeysMap = new HashMap<>();
    /* 关键字段对应的值 */
    static Map<String, List<LookupElement>> keyValuesMap = new HashMap<>();

    public static void addCustomLayoutTagLookElement(String tag) {
        Log.d(TAG, "addCustomLayoutTagLookElement() layout tag = " + tag);
        layoutTags.add(tag);
        customTagLookElements.add(LookupElementBuilder.create(tag).withInsertHandler(tagInsertHandler));
        createKeyLookupElement(tag);
    }

    public static void addCustomViewTagLookElement(String tag) {
        Log.d(TAG, "addCustomViewTagLookElement() view tag = " + tag);
        viewTags.add(tag);
        customTagLookElements.add(LookupElementBuilder.create(tag).withInsertHandler(tagInsertHandler));
        createKeyLookupElement(tag);
    }

    static final InsertHandler<LookupElement> tagInsertHandler = (context, lookupElement) -> {
        // add by HLM 确定当前的tag，自动填充width和height属性，同时自动关闭标签
        String tag = lookupElement.getLookupString();
        Editor editor = context.getEditor();
        Document document = editor.getDocument();
        int tailOffset = editor.getCaretModel().getOffset();
        String subString = document.getText().substring(0, tailOffset);
        String tailString = document.getText().substring(tailOffset);
        if (tailString.indexOf(">") < tailString.indexOf("<")) return; // 标签重命名
        if (subString.substring(subString.length() - tag.length() - 1).startsWith("/")) return; // 闭合标签
        // 获取新增标签前面的空白内容，用于后面的自动填充
        String whiteSpace = subString.substring(subString.lastIndexOf("\n") + 1, subString.lastIndexOf("<"));
        // 自动填充的属性内容
        String attrContent = "\n" +
                whiteSpace + "\t" + AttributeKey.WIDTH + "=\"" + MATCH_PARENT + "\"\n" +
                whiteSpace + "\t" + AttributeKey.HEIGHT + "=\"" + MATCH_PARENT + "\"";
        document.insertString(tailOffset, attrContent);
        tailOffset += attrContent.length(); // 增加自动填充属性内容的偏移
        String closeElement; // 自动闭合标签的内容
        if (layoutTags.contains(tag)) {
            closeElement = ">\n" + whiteSpace + "\t" + "\n" + whiteSpace + "</" + tag + ">";
            document.insertString(tailOffset, closeElement);
            // 移动光标到新的行，使得可以直接添加新的标签节点
            editor.getCaretModel().moveToOffset(tailOffset + 2 + whiteSpace.length() + "\t".length());
        } else if (viewTags.contains(tag)) {
            closeElement = "/>";
            document.insertString(tailOffset, closeElement);
            // 移动光标到所有属性的最后面，使得可以直接添加属性
            editor.getCaretModel().moveToOffset(tailOffset);
        } else if (tag.contains(".")) {
            closeElement = ">\n" + whiteSpace + "\t";
            document.insertString(tailOffset, closeElement);
            // 移动光标到新的行，使得可以直接添加新的标签节点
            editor.getCaretModel().moveToOffset(tailOffset + 1 + whiteSpace.length() + "\n\t".length());
        }
    };
    static final InsertHandler<LookupElement> keyInsertHandler = (context, lookupElement) -> {
        final Editor editor = context.getEditor();
        final Document document = editor.getDocument();
        int tailOffset = editor.getCaretModel().getOffset();
        document.insertString(tailOffset, "=\"");
        editor.getCaretModel().moveToOffset(tailOffset + 2);
        tailOffset += 2;
        LogicalPosition start = editor.offsetToLogicalPosition(tailOffset), end = null;
        switch (lookupElement.getLookupString()) {
            case AttributeKey.WIDTH:
            case AttributeKey.HEIGHT:
                // 需要先插入默认内容，再获取内容的最后的位置LogicalPosition，得到的才是准确的位置
                document.insertString(tailOffset, MATCH_PARENT + "\"");
                end = editor.offsetToLogicalPosition(tailOffset + MATCH_PARENT.length());
                break;
            case AttributeKey.ORIENTATION:
                document.insertString(tailOffset, HORIZONTAL + "\"");
                end = editor.offsetToLogicalPosition(tailOffset + HORIZONTAL.length());
                break;
            case AttributeKey.SRC:
                document.insertString(tailOffset, "mipmap/" + "\"");
                editor.getCaretModel().moveToOffset(tailOffset + "mipmap/".length());
                // 不需要选中内容，直接返回
                return;
            case AttributeKey.BACKGROUND:
                document.insertString(tailOffset, WHITE_COLOR + "\"");
                // 将光标移动到#号之后
                editor.getCaretModel().moveToOffset(tailOffset + 1);
                // 背景颜色从#符号之后开始选中（数字部分）
                start = editor.offsetToLogicalPosition(tailOffset + 1);
                end = editor.offsetToLogicalPosition(tailOffset + WHITE_COLOR.length());
                break;
            case AttributeKey.TOP_BORDER:
            case AttributeKey.BOTTOM_BORDER:
            case AttributeKey.LEFT_BORDER:
            case AttributeKey.RIGHT_BORDER:
                document.insertString(tailOffset, "1\"");
                // 将光标移动到数字之后
                editor.getCaretModel().moveToOffset(tailOffset + 1);
                // 背景颜色从#符号之后开始选中（数字部分）
                start = editor.offsetToLogicalPosition(tailOffset);
                end = editor.offsetToLogicalPosition(tailOffset + 1);
                break;
        }
        if (end == null) {
            document.insertString(tailOffset, "\"");
        } else {
            Log.d(TAG, "line = " + start.line + " , column = " + start.column);
            editor.getCaretModel().setCaretsAndSelections(Collections.singletonList(new CaretState(null, start, end)));
        }
    };

    static {
        layoutTags = new LinkedList<>(Arrays.asList("CenterLayout", "FrameLayout", "GridLayout", "LinearLayout"));
        viewTags = new LinkedList<>(Arrays.asList("Button", "CheckBox", "EditText", "ImageButton", "ImageView",
                "RecyclerView", "Span", "Spinner", "SwitchButton", "TextView"));
        createTagLookElement();

        createKeyLookupElement("CenterLayout", AttributeKey.ORIENTATION, AttributeKey.GRAVITY);
        createKeyLookupElement("FrameLayout", AttributeKey.ORIENTATION, AttributeKey.GRAVITY);
        createKeyLookupElement("LinearLayout", AttributeKey.ORIENTATION, AttributeKey.GRAVITY);
        createKeyLookupElement("GridLayout", AttributeKey.ORIENTATION, AttributeKey.GRAVITY, AttributeKey.column,
                AttributeKey.GAP, AttributeKey.GAP_VERTICAL, AttributeKey.GAP_HORIZONTAL);
        createKeyLookupElement("Button", AttributeKey.TEXT, AttributeKey.TEXT_SIZE, AttributeKey.TEXT_ALIGNMENT,
                AttributeKey.TEXT_STYLE, AttributeKey.TEXT_FONT, AttributeKey.TEXT_COLOR);
        createKeyLookupElement("CheckBox", AttributeKey.TEXT, AttributeKey.TEXT_SIZE, AttributeKey.TEXT_ALIGNMENT,
                AttributeKey.TEXT_STYLE, AttributeKey.TEXT_FONT, AttributeKey.TEXT_COLOR,
                AttributeKey.selectedBoxIconKey, AttributeKey.unselectedBoxIconKey, AttributeKey.isSelectedOnKey);
        createKeyLookupElement("EditText", AttributeKey.TEXT, AttributeKey.TEXT_SIZE,
                AttributeKey.TEXT_STYLE, AttributeKey.TEXT_FONT, AttributeKey.TEXT_COLOR, AttributeKey.HINT_TEXT);
        createKeyLookupElement("ImageButton", AttributeKey.SRC);
        createKeyLookupElement("ImageView", AttributeKey.SRC, AttributeKey.scaleType);
        createKeyLookupElement("RecyclerView");
        createKeyLookupElement("Span");
        createKeyLookupElement("Spinner", AttributeKey.arrayAttrKey, AttributeKey.selectedColorKey, AttributeKey.enterColorKey);
        createKeyLookupElement("SwitchButton", AttributeKey.switchOnKey, AttributeKey.switchOffKey, AttributeKey.isSwitchOnKey);
        createKeyLookupElement("TextView", AttributeKey.TEXT, AttributeKey.TEXT_SIZE, AttributeKey.TEXT_ALIGNMENT,
                AttributeKey.TEXT_STYLE, AttributeKey.TEXT_FONT, AttributeKey.TEXT_COLOR);

        createValueLookupElement(AttributeKey.WIDTH, MATCH_PARENT, WRAP_CONTENT);
        createValueLookupElement(AttributeKey.HEIGHT, MATCH_PARENT, WRAP_CONTENT);
        createValueLookupElement(AttributeKey.ORIENTATION, VERTICAL, HORIZONTAL);
        createValueLookupElement(AttributeKey.VISIBLE, VISIBLE, GONE);
        createValueLookupElement(AttributeKey.OPAQUE, OPAQUE, NOT_OPAQUE);
        createValueLookupElement(AttributeKey.isSwitchOnKey, TRUE, FALSE);
        createValueLookupElement(AttributeKey.isSelectedOnKey, TRUE, FALSE);
        createValueLookupElement(AttributeKey.TEXT_STYLE, SERIF, SANS_SERIF,
                DIALOG, DIALOG_INPUT, MONOSPACED);
        createValueLookupElement(AttributeKey.TEXT_FONT, PLAIN, BOLD, ITALIC);
        createValueLookupElement(AttributeKey.GRAVITY, left, right, top, bottom, center);
        createValueLookupElement(AttributeKey.TEXT_ALIGNMENT,
                left, right, top, bottom, center,
                leftAndTop, leftAndBottom, rightAndTop, rightAndBottom);
        createValueLookupElement(AttributeKey.scaleType, scaleFitCenter, scaleCenter,
                scaleFitXY, scaleFitStart, scaleFitEnd);

    }

    private static void createTagLookElement() {
        for (String layoutTag : layoutTags) {
            tagLookupElements.add(LookupElementBuilder.create(layoutTag).withInsertHandler(tagInsertHandler));
        }
        for (String viewTag : viewTags) {
            tagLookupElements.add(LookupElementBuilder.create(viewTag).withInsertHandler(tagInsertHandler));
        }
    }

    // 根据不同的视图标签，创建当前视图可选的属性key
    private static void createKeyLookupElement(String tag, String... keys) {
        for (String key : keys) {
            keyLookupElements.add(LookupElementBuilder.create(key).withInsertHandler(keyInsertHandler));
        }
        // 各种标签都具备的基本属性
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.ID).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.WIDTH).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.HEIGHT).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.BACKGROUND).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.VISIBLE).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.OPAQUE).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.MARGIN).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.MARGIN_LEFT).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.MARGIN_RIGHT).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.MARGIN_TOP).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.MARGIN_BOTTOM).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.TOP_BORDER).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.BOTTOM_BORDER).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.LEFT_BORDER).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.RIGHT_BORDER).withInsertHandler(keyInsertHandler));
        tagKeysMap.put(tag, new LinkedList<>(keyLookupElements));
        keyLookupElements.clear();
    }

    // 根据键入的属性key，创建属性可选的值
    public static void createValueLookupElement(String key, String... values) {
        for (String value : values) {
            valueLookupElements.add(LookupElementBuilder.create(value));
        }
        keyValuesMap.put(key, new LinkedList<>(valueLookupElements));
        valueLookupElements.clear();
    }

    public SimpleCompletionContributor() {
        super();
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SimpleTypes.TAG),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        Log.d(TAG, "completion as tag");
                        resultSet.addAllElements(tagLookupElements);
//                        Project project = (Project) context.get(CommonDataKeys.PROJECT);
//                        if (customTagLookElements.get(project) != null) {
                        if (customTagLookElements.size() > 0) {
                            List<LookupElement> copyCustomTags = new LinkedList<>(customTagLookElements);
                            for (LookupElement lookupElement : copyCustomTags) {
                                Log.d(this, "custom tag = " + lookupElement.getLookupString());
                                resultSet.addElement(lookupElement);
                            }
                        }
//                        }
                    }
                }
        );
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SimpleTypes.KEY),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        Log.d(TAG, "completion as key");
                        // add by HLM 确定当前的tag，根据tag提示相应的key列表
                        Editor editor = parameters.getEditor();
                        Document document = editor.getDocument();
                        int tailOffset = editor.getCaretModel().getOffset();
                        String subString = document.getText().substring(0, tailOffset);
                        subString = subString.substring(subString.lastIndexOf("<") + 1).trim();
                        String tag = subString.substring(0, subString.indexOf("\n")).trim();
                        if (tag.contains(" ")) {
                            tag = tag.substring(0, tag.indexOf(" "));
                        }
                        Log.d(TAG, "tag = " + tag);
                        keyLookupElements.clear();
                        keyLookupElements.addAll(tagKeysMap.get(tag) == null ? Collections.emptyList() : tagKeysMap.get(tag));
                        resultSet.addAllElements(keyLookupElements);
                    }
                }
        );
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SimpleTypes.VALUE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        Log.d(TAG, "completion as value");
                        // add by HLM 确定当前行中的key，根据key提示相应的内容填充列表
                        Editor editor = parameters.getEditor();
                        Document document = editor.getDocument();
                        int tailOffset = editor.getCaretModel().getOffset();
                        String subString = document.getText().substring(0, tailOffset);
                        String key = subString.substring(subString.lastIndexOf("\n"), subString.lastIndexOf("=")).trim();
                        if (key.contains(" ")) {
                            key = key.substring(key.lastIndexOf(" "));
                        }
                        Log.d(TAG, "key = " + key);
                        valueLookupElements.clear();
                        valueLookupElements.addAll(keyValuesMap.get(key) == null ? Collections.emptyList() : keyValuesMap.get(key));
                        resultSet.addAllElements(valueLookupElements);
                    }
                }
        );
    }

    @Nullable
    @Override
    public AutoCompletionDecision handleAutoCompletionPossibility(@NotNull AutoCompletionContext context) {
        Log.d(this, "handleAutoCompletionPossibility");
        return super.handleAutoCompletionPossibility(context);
    }

    @Nullable
    @Override
    public String handleEmptyLookup(@NotNull CompletionParameters parameters, Editor editor) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        String caretString = document.getText().substring(0, offset);
        Log.d(this, "caretString = " + caretString);
        return super.handleEmptyLookup(parameters, editor);
    }
}
