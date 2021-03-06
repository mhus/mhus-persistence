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

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.osgi.framework.InvalidSyntaxException;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.api.MOsgi;

public class AdbOsgiUtil {

    public static Class<? extends Object> getType(AdbService service, String typeName)
            throws IOException {
        for (Class<? extends Object> item : service.getManager().getSchema().getObjectTypes())
            if (item.getSimpleName().equals(typeName)) {
                return item;
            }
        throw new IOException("Type not found in service: " + typeName);
    }

    public static String getTableName(AdbService service, String typeName) throws IOException {
        typeName = typeName.toLowerCase();
        for (Class<? extends Object> item : service.getManager().getSchema().getObjectTypes())
            if (item.getSimpleName().toLowerCase().equals(typeName)) {
                return item.getCanonicalName();
            }
        throw new IOException("Type not found in service: " + typeName);
    }

    public static String getTableName(AdbService service, Class<?> type) throws IOException {
        for (Class<? extends Object> item : service.getManager().getSchema().getObjectTypes())
            if (item.getName().equals(type.getName())) {
                return item.getCanonicalName();
            }
        throw new IOException("Type not found in service: " + type);
    }

    public static List<AdbService> getServices(boolean connectedOnly) {

        LinkedList<AdbService> out = new LinkedList<>();
        for (AdbService service : MOsgi.getServices(AdbService.class, null))
            if (!connectedOnly || service.isConnected()) out.add(service);
        return out;
    }

    public static AdbService getService(String serviceName)
            throws IOException, InvalidSyntaxException {
        if (serviceName == null) throw new NullPointerException("service name is null");
        int cnt = 0;

        for (AdbService service : MOsgi.getServices(AdbService.class, null)) {
            if (serviceName.equals("*")
                    || serviceName.equals("*" + cnt)
                    || serviceName.equals(service.getServiceName())) return service;
            cnt++;
        }
        throw new IOException("Service not found: " + serviceName);
    }

    public static Object createAttribute(Class<?> type, Object value) {

        // TODO use escape -ing
        if (value == null || value.equals("[null]")) return null;
        if (value.equals("[uuid]")) return UUID.randomUUID();

        if (value instanceof String) {
            String str = (String) value;
            if (str.startsWith("[") && str.endsWith("]")) {
                String[] parts = str.substring(1, str.length() - 1).split(",");
                for (int i = 0; i < parts.length; i++) parts[i] = MUri.decode(parts[i]);
                value = parts;
            } else {
                value = MUri.decode(str);
            }
        }

        if (type == value.getClass()) return value;

        if (type == int.class || type == Integer.class) return MCast.toint(value, 0);

        if (type == long.class || type == Long.class) return MCast.tolong(value, 0);

        if (type == float.class || type == Float.class) return MCast.tofloat(value, 0);

        if (type == double.class || type == Double.class) return MCast.todouble(value, 0);

        if (type == boolean.class || type == Boolean.class) return MCast.toboolean(value, false);

        if (type == Date.class) return MCast.toDate(String.valueOf(value), null);

        if (type == java.sql.Date.class) {
            Date data = MCast.toDate(String.valueOf(value), null);
            if (data == null) return null;
            return new java.sql.Date(data.getTime());
        }

        if (type == UUID.class) return UUID.fromString(String.valueOf(value));

        if (type.isEnum()) return String.valueOf(value);

        return null;
    }

    public static List<Object> getObjects(AdbService service, Class<?> type, String id)
            throws MException {
        LinkedList<Object> out = new LinkedList<>();
        if (MString.isEmptyTrim(id)) return out;

        if (id.startsWith("(") && id.endsWith(")")) {
            String aql = id.substring(1, id.length() - 1);
            for (Object o : service.getManager().getByQualification(type, aql, null)) out.add(o);
        } else {
            // TODO separate PKs, currently only one PK is supported
            Object obj = service.getManager().getObject(type, id);
            if (obj != null) out.add(obj);
        }

        return out;
    }

    public static AdbService getCommonAdbService() {
        try {
            AdbService service = getService("common_adb");
            if (service == null) MLogUtil.log().i("CommonAdbService not found");
            return service;
        } catch (Throwable t) {
            MLogUtil.log().i(t.toString());
            return null;
        }
    }
}
