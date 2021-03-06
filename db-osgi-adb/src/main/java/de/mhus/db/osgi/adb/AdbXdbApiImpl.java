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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Component;

import de.mhus.db.osgi.api.adb.AdbOsgiUtil;
import de.mhus.db.osgi.api.adb.AdbService;
import de.mhus.db.osgi.api.xdb.XdbApi;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.xdb.XdbService;
import de.mhus.lib.xdb.XdbType;

/**
 * Singleton Service
 *
 * <p>Provide a XdbApi and implement the Adb schema. Searches for AdbManagerService and provide it
 * as XdbService to Xdb.
 *
 * @author mikehummel
 */
@Component(property = "xdb.type=adb")
public class AdbXdbApiImpl implements XdbApi {

    @Override
    public XdbService getService(String serviceName) throws NotFoundException {
        try {
            AdbService service = AdbOsgiUtil.getService(serviceName);

            return service.getManager();

        } catch (IOException | InvalidSyntaxException e) {
            throw new NotFoundException("Service not found", serviceName, e);
        }
    }

    @Override
    public <T> XdbType<T> getType(String serviceName, String typeName) throws NotFoundException {
        try {
            AdbService service = AdbOsgiUtil.getService(serviceName);

            String tableName = AdbOsgiUtil.getTableName(service, typeName);
            return service.getManager().getType(tableName);

        } catch (IOException | InvalidSyntaxException e) {
            throw new NotFoundException("Service not found", serviceName, e);
        }
    }

    @Override
    public List<String> getServiceNames() {
        LinkedList<String> out = new LinkedList<>();
        for (AdbService s : AdbOsgiUtil.getServices(false)) {
            out.add(s.getServiceName());
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I> I adaptTo(Class<? extends I> ifc) {
        return (I) this;
    }
}
