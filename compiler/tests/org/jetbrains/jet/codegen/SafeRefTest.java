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

package org.jetbrains.jet.codegen;

import org.jetbrains.jet.ConfigurationKind;

public class SafeRefTest extends CodegenTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createEnvironmentWithMockJdkAndIdeaAnnotations(ConfigurationKind.JDK_AND_ANNOTATIONS);
    }

    public void test247 () throws Exception {
        blackBoxFile("regressions/kt247.jet");
    }

    public void test245 () throws Exception {
        blackBoxFile("regressions/kt245.jet");
    }

    public void test232 () throws Exception {
        blackBoxFile("regressions/kt232.jet");
    }

    public void test1572 () throws Exception {
        blackBoxFile("regressions/kt1572.jet");
    }
}
