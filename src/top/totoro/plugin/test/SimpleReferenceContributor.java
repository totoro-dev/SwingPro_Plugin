package top.totoro.plugin.test;
// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import top.totoro.plugin.file.Log;

import static top.totoro.plugin.test.SimpleAnnotator.SIMPLE_PREFIX_STR;
import static top.totoro.plugin.test.SimpleAnnotator.SIMPLE_SEPARATOR_STR;

public class SimpleReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext context) {
                        PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                        String value = literalExpression.getValue() instanceof String ?
                                (String) literalExpression.getValue() : null;
                        if ((value != null && value.startsWith(SIMPLE_PREFIX_STR + SIMPLE_SEPARATOR_STR))) {
                            TextRange property = new TextRange(SIMPLE_PREFIX_STR.length() + SIMPLE_SEPARATOR_STR.length() + 1,
                                    value.length() + 1);
                            Log.d("SimpleReferenceContributor","has a value : "+((PsiLiteralExpression) element).getValue());
                            return new PsiReference[]{new SimpleReference(element, property)};
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}
