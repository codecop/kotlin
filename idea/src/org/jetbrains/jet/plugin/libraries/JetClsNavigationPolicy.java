/*
 * Copyright 2000-2012 JetBrains s.r.o.
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

package org.jetbrains.jet.plugin.libraries;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.compiled.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.psi.JetDeclaration;

/**
 * @author Evgeny Gerashchenko
 * @since 3/2/12
 */
public class JetClsNavigationPolicy implements ClsCustomNavigationPolicy {
    @Override
    @Nullable
    public PsiElement getNavigationElement(@NotNull ClsClassImpl clsClass) {
        return getJetDeclarationByClsElement(clsClass);
    }

    @Override
    @Nullable
    public PsiElement getNavigationElement(@NotNull ClsMethodImpl clsMethod) {
        return getJetDeclarationByClsElement(clsMethod);
    }

    @Override
    @Nullable
    public PsiElement getNavigationElement(@NotNull ClsFieldImpl clsField) {
        return getJetDeclarationByClsElement(clsField);
    }

    @Nullable
    private static JetDeclaration getJetDeclarationByClsElement(ClsElementImpl clsElement) {
        VirtualFile virtualFile = clsElement.getContainingFile().getVirtualFile();
        if (virtualFile == null || !JetDecompiledData.isKotlinFile(clsElement.getProject(), virtualFile)) {
            return null;
        }
        JetDecompiledData decompiledData = JetDecompiledData.getDecompiledData((ClsFileImpl) clsElement.getContainingFile());
        return decompiledData.getJetDeclarationByClsElement(clsElement);
    }
}