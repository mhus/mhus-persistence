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
package de.mhus.db.karaf.xdb.cmd;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.db.karaf.xdb.adb.XdbKarafUtil;
import de.mhus.db.osgi.api.xdb.XdbUtil;
import de.mhus.lib.core.MString;
import de.mhus.lib.xdb.XdbType;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(
        scope = "xdb",
        name = "clone",
        description = "Load a object out of the database and store it as a clone.")
@Service
public class CmdClone extends AbstractCmd {

    @Argument(
            index = 0,
            name = "type",
            required = true,
            description = "Type to select",
            multiValued = false)
    String typeName;

    @Argument(
            index = 1,
            name = "search",
            required = true,
            description = "Id of the object to clone or query",
            multiValued = false)
    String search;

    @Argument(
            index = 2,
            name = "attributes",
            required = false,
            description = "Attributes for the initial creation",
            multiValued = true)
    String[] attributes;

    @Option(name = "-x", description = "Output parameter", required = false)
    String outputParam = null;

    @Option(name = "-a", description = "Api Name", required = false)
    String apiName;

    @Option(name = "-s", description = "Service Name", required = false)
    String serviceName;

    {
        cmdIsPermissionDependent = true;
    }

    @Override
    public Object execute2() throws Exception {

        apiName = XdbKarafUtil.getApiName(getSession(), apiName);
        serviceName = XdbKarafUtil.getServiceName(getSession(), serviceName);

        Object output = null;

        XdbType<?> type = XdbKarafUtil.getType(apiName, serviceName, typeName);

        for (Object object : XdbUtil.createObjectList(type, search, null)) {

            System.out.println(">>> CLONE " + object);
            HashMap<String, Object> attrObj = null;
            attrObj = new HashMap<>();
            if (attributes != null) {
                for (String item : attributes) {
                    String key = MString.beforeIndex(item, '=').trim();
                    String value = MString.afterIndex(item, '=').trim();
                    attrObj.put(key, value);
                }
            }

            for (Entry<String, Object> entry : attrObj.entrySet()) {
                String name = entry.getKey();
                Object v = XdbUtil.prepareValue(type, name, entry.getValue());
                System.out.println("--- SET " + name + "  = " + v);
                XdbUtil.setValue(type, object, name, v);
            }

            //			for (String name : type.getAttributeNames()) {
            //				if (attrObj.containsKey(name)) {
            //					Object v = type.prepareValue(name, attrObj.get(name) );
            //					System.out.println("--- SET " + name + "  = " + v );
            //					XdbUtil.setValue(type, object, name, v);
            //				}
            //			}

            System.out.print("*** CREATE ");
            type.createObject(object);
            System.out.println(type.getIdAsString(object));

            output = object;
        }

        if (outputParam != null) getSession().put(outputParam, output);
        return null;

        /*
        		DbManagerService service = AdbUtil.getService(serviceName);
        		Class<?> type = AdbUtil.getType(service, typeName);
        		String regName = service.getManager().getRegistryName(type);
        		Table tableInfo = service.getManager().getTable(regName);

        		for (Object object : AdbUtil.getObjects(service, type, id)) {

        			System.out.println(">>> CLONE " + object);
        			HashMap<String, Object> attrObj = null;
        			attrObj = new HashMap<>();
        			if (attributes != null) {
        				for (String item : attributes) {
        					String key = MString.beforeIndex(item, '=').trim();
        					String value = MString.afterIndex(item, '=').trim();
        					attrObj.put(key, value);
        				}
        			}

        			for (Field f : tableInfo.getFields()) {
        				if (attrObj.containsKey(f.getName())) {
        					Object v = AdbUtil.createAttribute(f.getType(), attrObj.get(f.getName()) );
        					System.out.println("--- SET " + f.getName() + "  = " + v );
        					f.set(object, v);
        				}
        			}

        			System.out.print("*** CREATE");
        			service.getManager().createObject(regName, object);
        			for (Field f : tableInfo.getPrimaryKeys()) {
        				System.out.print(" ");
        				System.out.print(f.get(object));
        			}
        			output = object;
        			System.out.println();
        		}
        		if (outputParam != null)
        			session.put(outputParam, output);
        		return null;
        */
    }
}
