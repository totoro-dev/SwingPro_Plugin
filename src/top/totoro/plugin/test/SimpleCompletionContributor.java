package top.totoro.plugin.test;
// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.InsertHandlerDecorator;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.openapi.editor.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.Consumer;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.EmptyIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.totoro.plugin.constant.AttributeDefaultValue;
import top.totoro.plugin.constant.AttributeKey;
import top.totoro.plugin.file.Log;

import java.lang.reflect.Array;
import java.util.*;

public class SimpleCompletionContributor extends CompletionContributor {

    static List<LookupElement> tagLookupElements = new LinkedList<>();
    static List<LookupElement> keyLookupElements = new LinkedList<>();
    static List<LookupElement> valueLookupElements = new LinkedList<>();

    static {
        tagLookupElements.add(LookupElementBuilder.create("LinearLayout"));
        tagLookupElements.add(LookupElementBuilder.create("TextView"));
        tagLookupElements.add(LookupElementBuilder.create("ImageView"));
        tagLookupElements.add(LookupElementBuilder.create("ImageButton"));
        tagLookupElements.add(LookupElementBuilder.create("Span"));
        tagLookupElements.add(LookupElementBuilder.create("Spinner"));
        tagLookupElements.add(LookupElementBuilder.create("SwitchButton"));
        tagLookupElements.add(LookupElementBuilder.create("SwitchButton"));

        final InsertHandler<LookupElement> keyInsertHandler = (context, lookupElement) -> {
            Log.d("SimpleCompletionContributor", "insert key width");
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
                    document.insertString(tailOffset, AttributeDefaultValue.MATCH_PARENT + "\"");
                    end = editor.offsetToLogicalPosition(tailOffset + AttributeDefaultValue.MATCH_PARENT.length());
                    break;
                case AttributeKey.ORIENTATION:
                    document.insertString(tailOffset, AttributeDefaultValue.HORIZONTAL + "\"");
                    end = editor.offsetToLogicalPosition(tailOffset + AttributeDefaultValue.HORIZONTAL.length());
                    break;
                case AttributeKey.SRC:
                    document.insertString(tailOffset, "mipmap/" + "\"");
                    editor.getCaretModel().moveToOffset(tailOffset + "mipmap/".length());
                    // 不需要选中内容，直接返回
                    return;
                case AttributeKey.BACKGROUND:
                    document.insertString(tailOffset, AttributeDefaultValue.WHITE_COLOR + "\"");
                    // 将光标移动到#号之后
                    editor.getCaretModel().moveToOffset(tailOffset + 1);
                    // 背景颜色从#符号之后开始选中（数字部分）
                    start = editor.offsetToLogicalPosition(tailOffset + 1);
                    end = editor.offsetToLogicalPosition(tailOffset + AttributeDefaultValue.WHITE_COLOR.length());
                    break;
            }
            if (end == null) {
                document.insertString(tailOffset, "\"");
            } else {
                Log.d("SimpleCompletionContributor", "line = " + start.line + " , column = " + start.column);
                editor.getCaretModel().setCaretsAndSelections(Collections.singletonList(new CaretState(null, start, end)));
            }
        };
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.WIDTH).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.HEIGHT).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.ID).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.ORIENTATION).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.BACKGROUND).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.VISIBLE).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.SRC).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.TEXT).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.TEXT_SIZE).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.TEXT_STYLE).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.TEXT_FONT).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.TEXT_COLOR).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create(AttributeKey.HINT_TEXT).withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create("array").withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create("selectedColor").withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create("enterColor").withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create("switchOnIcon").withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create("switchOffIcon").withInsertHandler(keyInsertHandler));
        keyLookupElements.add(LookupElementBuilder.create("isSwitchOn").withInsertHandler(keyInsertHandler));

        valueLookupElements.add(LookupElementBuilder.create("match_parent"));
        valueLookupElements.add(LookupElementBuilder.create("wrap_content"));
        valueLookupElements.add(LookupElementBuilder.create("horizontal"));
        valueLookupElements.add(LookupElementBuilder.create("vertical"));
        valueLookupElements.add(LookupElementBuilder.create("gone"));
        valueLookupElements.add(LookupElementBuilder.create("visible"));
        valueLookupElements.add(LookupElementBuilder.create("true"));
        valueLookupElements.add(LookupElementBuilder.create("false"));
        valueLookupElements.add(LookupElementBuilder.create("serif"));
        valueLookupElements.add(LookupElementBuilder.create("sans_serif"));
        valueLookupElements.add(LookupElementBuilder.create("dialog"));
        valueLookupElements.add(LookupElementBuilder.create("dialog_input"));
        valueLookupElements.add(LookupElementBuilder.create("monospaced"));
        valueLookupElements.add(LookupElementBuilder.create("plain"));
        valueLookupElements.add(LookupElementBuilder.create("bold"));
        valueLookupElements.add(LookupElementBuilder.create("italic"));
    }

    public SimpleCompletionContributor() {
        super();
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SimpleTypes.TAG),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addAllElements(tagLookupElements);
                    }
                }
        );
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SimpleTypes.KEY),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet result) {
                        result.addAllElements(keyLookupElements);
                    }
                }
        );
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SimpleTypes.VALUE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
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
