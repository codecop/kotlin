/*
 * Copyright 2010-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.k2js.translate.expression;

import com.google.dart.compiler.backend.js.ast.JsExpression;
import com.google.dart.compiler.backend.js.ast.JsInvocation;
import com.google.dart.compiler.backend.js.ast.JsName;
import com.google.dart.compiler.backend.js.ast.JsNameRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.ClassDescriptor;
import org.jetbrains.jet.lang.psi.*;
import org.jetbrains.k2js.translate.context.TranslationContext;
import org.jetbrains.k2js.translate.general.AbstractTranslator;
import org.jetbrains.k2js.translate.general.Translation;
import org.jetbrains.k2js.translate.utils.BindingUtils;
import org.jetbrains.k2js.translate.utils.TranslationUtils;

import static org.jetbrains.k2js.translate.utils.JsAstUtils.*;

/**
 * @author Pavel Talanov
 */
public final class PatternTranslator extends AbstractTranslator {

    @NotNull
    public static PatternTranslator newInstance(@NotNull TranslationContext context) {
        return new PatternTranslator(context);
    }

    private PatternTranslator(@NotNull TranslationContext context) {
        super(context);
    }

    @NotNull
    public JsExpression translateIsExpression(@NotNull JetIsExpression expression) {
        JsExpression left = Translation.translateAsExpression(expression.getLeftHandSide(), context());
        JetTypeReference typeReference = expression.getTypeRef();
        assert typeReference != null;
        JsExpression result = translateIsCheck(left, typeReference);
        if (expression.isNegated()) {
            return negated(result);
        }
        return result;
    }

    @NotNull
    public JsExpression translateIsCheck(@NotNull JsExpression subject, @NotNull JetTypeReference typeReference) {
        JsExpression result = translateAsIntrinsicTypeCheck(subject, typeReference);
        if (result != null) {
            return result;
        }
        return translateAsIsCheck(subject, typeReference);
    }

    @NotNull
    private JsExpression translateAsIsCheck(@NotNull JsExpression expressionToMatch,
                                            @NotNull JetTypeReference typeReference) {
        JsInvocation isCheck = new JsInvocation(context().namer().isOperationReference(),
                                                     expressionToMatch, getClassReference(typeReference));
        if (isNullable(typeReference)) {
            return addNullCheck(expressionToMatch, isCheck);
        }
        return isCheck;
    }

    @Nullable
    private JsExpression translateAsIntrinsicTypeCheck(@NotNull JsExpression expressionToMatch,
                                                       @NotNull JetTypeReference typeReference) {
        JsExpression result = null;
        JsName className = getClassReference(typeReference).getName();
        if (className.getIdent().equals("String")) {
            result = typeof(expressionToMatch, program().getStringLiteral("string"));
        }
        if (className.getIdent().equals("Int")) {
            result = typeof(expressionToMatch, program().getStringLiteral("number"));
        }
        return result;
    }

    @NotNull
    private static JsExpression addNullCheck(@NotNull JsExpression expressionToMatch, @NotNull JsInvocation isCheck) {
        return or(TranslationUtils.isNullCheck(expressionToMatch), isCheck);
    }

    private boolean isNullable(JetTypeReference typeReference) {
        return BindingUtils.getTypeByReference(bindingContext(), typeReference).isNullable();
    }

    @NotNull
    private JsNameRef getClassReference(@NotNull JetTypeReference typeReference) {
        return getClassNameReferenceForTypeReference(typeReference);
    }

    @NotNull
    private JsNameRef getClassNameReferenceForTypeReference(@NotNull JetTypeReference typeReference) {
        ClassDescriptor referencedClass = BindingUtils.getClassDescriptorForTypeReference
            (bindingContext(), typeReference);
        return TranslationUtils.getQualifiedReference(context(), referencedClass);
    }

    @NotNull
    public JsExpression translateExpressionPattern(@NotNull JsExpression expressionToMatch, @NotNull JetExpression patternExpression) {
        JsExpression expressionToMatchAgainst = translateExpressionForExpressionPattern(patternExpression);
        return equality(expressionToMatch, expressionToMatchAgainst);
    }

    @NotNull
    public JsExpression translateExpressionForExpressionPattern(@NotNull JetExpression patternExpression) {
        return Translation.translateAsExpression(patternExpression, context());
    }
}
