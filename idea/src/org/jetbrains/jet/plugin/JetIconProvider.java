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

package org.jetbrains.jet.plugin;

import com.intellij.ide.IconProvider;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.*;
import org.jetbrains.jet.lexer.JetTokens;

import javax.swing.*;
import java.util.List;

/**
 * @author yole
 */
public class JetIconProvider extends IconProvider {

    public static JetIconProvider INSTANCE = new JetIconProvider();

    @Override
    public Icon getIcon(@NotNull PsiElement psiElement, int flags) {
        if (psiElement instanceof JetFile) {
            JetFile file = (JetFile) psiElement;
            List<JetDeclaration> declarations = file.getDeclarations();
            JetClassOrObject mostImportantClass = null;
            String nameWithoutExtension = StringUtil.getPackageName(file.getName());
            for (JetDeclaration declaration : declarations) {
                if (mostImportantClass == null && declaration instanceof JetClassOrObject) {
                    mostImportantClass = (JetClassOrObject) declaration;
                }
                else if (declaration instanceof JetClassOrObject) {
                    JetClassOrObject object = (JetClassOrObject) declaration;
                    if (nameWithoutExtension.equals(object.getName())) {
                        mostImportantClass = object;
                    }
                }
            }
            if (mostImportantClass != null) {
                return getIcon(mostImportantClass, flags);
            }
            return JetIcons.FILE;
        }
        if (psiElement instanceof JetNamespaceHeader) {
            return (flags & Iconable.ICON_FLAG_OPEN) != 0 ? PlatformIcons.PACKAGE_OPEN_ICON : PlatformIcons.PACKAGE_ICON;
        }
        if (psiElement instanceof JetNamedFunction) {
            return PsiTreeUtil.getParentOfType(psiElement, JetNamedDeclaration.class) instanceof JetClass
                   ? JetIcons.LAMBDA
                   : JetIcons.FUNCTION;
        }
        if (psiElement instanceof JetClass) {
            JetClass jetClass = (JetClass) psiElement;
            if (jetClass.isTrait()) {
                return JetIcons.TRAIT;
            }

            Icon icon = jetClass.hasModifier(JetTokens.ENUM_KEYWORD) ? PlatformIcons.ENUM_ICON : PlatformIcons.CLASS_ICON;
            if (jetClass instanceof JetEnumEntry) {
                JetEnumEntry enumEntry = (JetEnumEntry) jetClass;
                if (enumEntry.getPrimaryConstructorParameterList() == null) {
                    icon = PlatformIcons.ENUM_ICON;
                }
            }
            return icon;
        }
        if (psiElement instanceof JetObjectDeclaration || psiElement instanceof JetClassObject) {
            return JetIcons.OBJECT;
        }
        if (psiElement instanceof JetParameter) {
            JetParameter parameter = (JetParameter)psiElement;
            if (parameter.getValOrVarNode() != null) {
                JetParameterList parameterList = PsiTreeUtil.getParentOfType(psiElement, JetParameterList.class);
                if (parameterList != null && parameterList.getParent() instanceof JetClass) {
                    return parameter.isVarArg() ? JetIcons.VAR : JetIcons.VAL;
                }
            }
            return JetIcons.PARAMETER;
        }
        if (psiElement instanceof JetProperty) {
            JetProperty property = (JetProperty)psiElement;
            return property.isVar() ? JetIcons.VAR : JetIcons.VAL;
        }
        return null;
    }
}