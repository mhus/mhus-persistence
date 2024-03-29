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
package de.mhus.db.osgi.adb;

import java.util.List;

import de.mhus.db.osgi.api.adb.AbstractDbSchema;
import de.mhus.db.osgi.api.adb.CommonDbConsumer;
import de.mhus.lib.adb.DbAccessManager;
import de.mhus.lib.adb.DbManager;
import de.mhus.lib.adb.DbMetadata;
import de.mhus.lib.adb.DbObject;
import de.mhus.lib.adb.model.Table;
import de.mhus.lib.adb.transaction.DbLockObject;
import de.mhus.lib.core.aaa.Aaa;
import de.mhus.lib.errors.AccessDeniedException;
import de.mhus.lib.sql.DbConnection;
import de.mhus.lib.sql.DbResult;

/**
 * Common used DB Schema, created by CommonAdbService for all CommonAdbProvider services.
 *
 * @author mikehummel
 */
public abstract class AbstractCommonDbSchema extends AbstractDbSchema {

    protected AbstractCommonService admin;
    protected DbAccessManager accessManager;

    public AbstractCommonDbSchema(AbstractCommonService admin) {
        this.admin = admin;
        init();
    }

    protected abstract void init();

    @Override
    public void findObjectTypes(List<Class<? extends Object>> list) {

        list.add(DbLockObject.class); // needed for object locking

        for (CommonDbConsumer schema : admin.getConsumer()) {
            schema.registerObjectTypes(list);
        }
    }

    @Override
    public Object createObject(
            Class<?> clazz,
            String registryName,
            DbResult ret,
            DbManager manager,
            boolean isPersistent)
            throws Exception {
        Object object = clazz.getDeclaredConstructor().newInstance();
        if (object instanceof DbObject) {
            ((DbObject) object).doInit(manager, registryName, isPersistent);
        }
        return object;
    }

    @Override
    public synchronized DbAccessManager getAccessManager(Table c) {
        if (accessManager == null) accessManager = new MyAccessManager();
        return accessManager;
    }

    protected class MyAccessManager extends DbAccessManager {

        @Override
        public void hasAccess(
                DbManager manager, Table c, DbConnection con, Object object, ACCESS right)
                throws AccessDeniedException {

            if (object instanceof DbMetadata) {
                DbMetadata obj = (DbMetadata) object;
                try {

                    switch (right) {
                        case CREATE:
                            if (!admin.canCreate(obj))
                                throw new AccessDeniedException(
                                        Aaa.getPrincipal(), c.getName(), right);
                            break;
                        case DELETE:
                            if (!admin.canDelete(obj))
                                throw new AccessDeniedException(
                                        Aaa.getPrincipal(), c.getName(), right);
                            break;
                        case READ:
                            if (!admin.canRead(obj))
                                throw new AccessDeniedException(
                                        Aaa.getPrincipal(), c.getName(), right);
                            break;
                        case UPDATE:
                            if (!admin.canUpdate(obj))
                                throw new AccessDeniedException(
                                        Aaa.getPrincipal(), c.getName(), right);
                            break;
                        default:
                            throw new AccessDeniedException(
                                    Aaa.getPrincipal(), c.getName(), right, "unknown right");
                    }
                } catch (AccessDeniedException ade) {
                    throw ade;
                } catch (Throwable t) {
                    log().d(t);
                    throw new AccessDeniedException(Aaa.getPrincipal(), c.getName(), right, t);
                }
            }
        }
    }
}
