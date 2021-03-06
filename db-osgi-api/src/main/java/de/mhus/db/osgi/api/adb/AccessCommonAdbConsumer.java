/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.db.osgi.api.adb;

import java.util.UUID;

import org.apache.shiro.authz.AuthorizationException;

import de.mhus.lib.annotations.generic.ExplicitAccess;
import de.mhus.lib.basics.UuidIdentificable;
import de.mhus.lib.core.aaa.Aaa;
import de.mhus.lib.errors.MException;

public abstract class AccessCommonAdbConsumer extends AbstractCommonAdbConsumer {

    @Override
    public boolean canCreate(Object obj) throws MException {
        // TODO need to check for each action
        Class<?> clazz = obj.getClass();
        if (Aaa.isAnnotated(clazz)) {
            try {
                Aaa.checkPermission(clazz);
            } catch (AuthorizationException e) {
                return false;
            }
            if (clazz.getAnnotation(ExplicitAccess.class) != null) return true;
        }
        String type = clazz.getCanonicalName();
        String ident = "*";
        if (obj instanceof UuidIdentificable) {
            UUID uuid = ((UuidIdentificable) obj).getId();
            if (uuid != null) ident = uuid.toString();
        }
        return Aaa.isPermitted(type + ":create:" + ident);
    }

    @Override
    public boolean canRead(Object obj) throws MException {
        Class<?> clazz = obj.getClass();
        if (Aaa.isAnnotated(clazz)) {
            try {
                Aaa.checkPermission(clazz);
            } catch (AuthorizationException e) {
                return false;
            }
            if (clazz.getAnnotation(ExplicitAccess.class) != null) return true;
        }
        String type = clazz.getCanonicalName();
        String ident = "*";
        if (obj instanceof UuidIdentificable) {
            UUID uuid = ((UuidIdentificable) obj).getId();
            if (uuid != null) ident = uuid.toString();
        }
        return Aaa.isPermitted(type + ":read:" + ident);
    }

    @Override
    public boolean canUpdate(Object obj) throws MException {
        Class<?> clazz = obj.getClass();
        if (Aaa.isAnnotated(clazz)) {
            try {
                Aaa.checkPermission(clazz);
            } catch (AuthorizationException e) {
                return false;
            }
            if (clazz.getAnnotation(ExplicitAccess.class) != null) return true;
        }
        String type = clazz.getCanonicalName();
        String ident = "*";
        if (obj instanceof UuidIdentificable) {
            UUID uuid = ((UuidIdentificable) obj).getId();
            if (uuid != null) ident = uuid.toString();
        }
        return Aaa.isPermitted(type + ":update:" + ident);
    }

    @Override
    public boolean canDelete(Object obj) throws MException {
        Class<?> clazz = obj.getClass();
        if (Aaa.isAnnotated(clazz)) {
            try {
                Aaa.checkPermission(clazz);
            } catch (AuthorizationException e) {
                return false;
            }
            if (clazz.getAnnotation(ExplicitAccess.class) != null) return true;
        }
        String type = clazz.getCanonicalName();
        String ident = "*";
        if (obj instanceof UuidIdentificable) {
            UUID uuid = ((UuidIdentificable) obj).getId();
            if (uuid != null) ident = uuid.toString();
        }
        return Aaa.isPermitted(type + ":delete:" + ident);
    }
}
