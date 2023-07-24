/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble.loader.sandbox;
import java.security.Permission;

/**
 * 将一个 File 对象所代表的路径作为根路径。
 * @version : 2021-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
class SecSandbox extends SecurityManager {
    private final SecurityManager parent;

    public SecSandbox(SecurityManager parent) {
        this.parent = parent;
    }

    private SecPolicyContext findPolicyContext() {
        Class<?>[] context = this.getClassContext();
        for (Class<?> atClass : context) {
            if (atClass.getClassLoader() instanceof SecPolicyContext) {
                return (SecPolicyContext) atClass.getClassLoader();
            }
        }
        return null;
    }

    @Override
    public void checkPermission(Permission perm) {
        SecPolicyContext policyContext = findPolicyContext();
        if (policyContext != null && policyContext.enable()) {
            policyContext.checkPermission(perm);
            return;
        }
        if (this.parent != null) {
            this.parent.checkPermission(perm);
        } else {
            super.checkPermission(perm);
        }
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        SecPolicyContext policyContext = findPolicyContext();
        if (policyContext != null && policyContext.enable()) {
            policyContext.checkPermission(perm);
            return;
        }
        if (this.parent != null) {
            this.parent.checkPermission(perm, context);
        } else {
            super.checkPermission(perm);
        }
    }
}
