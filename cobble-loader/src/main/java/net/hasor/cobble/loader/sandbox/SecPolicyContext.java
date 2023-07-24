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
public interface SecPolicyContext {
    boolean enable();

    /**
     * Throws a {@code SecurityException} if the requested
     * access, specified by the given permission, is not permitted based
     * on the security policy currently in effect.
     * <p>
     * This method calls {@code AccessController.checkPermission}
     * with the given permission.
     *
     * @param     perm   the requested permission.
     * @throws SecurityException if access is not permitted based on
     *            the current security policy.
     * @throws NullPointerException if the permission argument is
     *            {@code null}.
     * @since 1.2
     */
    void checkPermission(Permission perm);

    /**
     * Throws a {@code SecurityException} if the
     * specified security context is denied access to the resource
     * specified by the given permission.
     * The context must be a security
     * context returned by a previous call to
     * {@code getSecurityContext} and the access control
     * decision is based upon the configured security policy for
     * that security context.
     * <p>
     * If {@code context} is an instance of
     * {@code AccessControlContext} then the
     * {@code AccessControlContext.checkPermission} method is
     * invoked with the specified permission.
     * <p>
     * If {@code context} is not an instance of
     * {@code AccessControlContext} then a
     * {@code SecurityException} is thrown.
     *
     * @param      perm      the specified permission
     * @param      context   a system-dependent security context.
     * @throws SecurityException  if the specified security context
     *             is not an instance of {@code AccessControlContext}
     *             (e.g., is {@code null}), or is denied access to the
     *             resource specified by the given permission.
     * @throws NullPointerException if the permission argument is
     *             {@code null}.
     * @see        java.lang.SecurityManager#getSecurityContext()
     * @see java.security.AccessControlContext#checkPermission(java.security.Permission)
     * @since 1.2
     */
    public void checkPermission(Permission perm, Object context);
}
