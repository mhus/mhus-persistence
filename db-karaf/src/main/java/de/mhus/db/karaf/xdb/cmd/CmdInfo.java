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

import java.util.Comparator;
import java.util.List;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.db.karaf.xdb.adb.XdbKarafUtil;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.lib.xdb.XdbType;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "xdb", name = "info", description = "Show information of a type")
@Service
public class CmdInfo extends AbstractCmd {

    @Argument(
            index = 0,
            name = "type",
            required = true,
            description = "Type to select",
            multiValued = false)
    String typeName;

    @Option(name = "-a", description = "Api Name", required = false)
    String apiName;

    @Option(name = "-s", description = "Service Name", required = false)
    String serviceName;

    @Override
    public Object execute2() throws Exception {

        apiName = XdbKarafUtil.getApiName(getSession(), apiName);
        serviceName = XdbKarafUtil.getServiceName(getSession(), serviceName);

        XdbType<?> type = XdbKarafUtil.getType(apiName, serviceName, typeName);

        List<String> fieldNames = type.getAttributeNames();
        fieldNames.sort(
                new Comparator<String>() {

                    @Override
                    public int compare(String o1, String o2) {
                        boolean pk1 = type.isPrimaryKey(o1);
                        boolean pk2 = type.isPrimaryKey(o2);
                        if (pk1 == pk2) return o1.compareTo(o2);
                        if (pk1) return -1;
                        // if (pk2) return 1;
                        return 1;
                    }
                });

        ConsoleTable out = new ConsoleTable(tblOpt);
        out.setHeaderValues("Field Name", "Type", "PrimaryKey", "Persistent", "Mapping");
        for (String name : fieldNames) {
            out.addRowValues(
                    name,
                    type.getAttributeType(name),
                    type.isPrimaryKey(name),
                    type.isPersistent(name),
                    type.getTechnicalName(name));
        }
        /*
        		DbManagerService service = AdbUtil.getService(serviceName);
        		Class<?> type = AdbUtil.getType(service, typeName);

        		String regName = service.getManager().getRegistryName(type);
        		Table tableInfo = service.getManager().getTable(regName);

        		ConsoleTable out = new ConsoleTable(tblOpt);
        		out.setHeaderValues("Field Name","Type","PrimaryKey","Persistent","Mapping");

        		LinkedList<String> primaryNames = new LinkedList<>();
        		for (Field f : tableInfo.getPrimaryKeys())
        			primaryNames.add(f.getName());

        		for (Field f : tableInfo.getFields())
        			out.addRowValues(
        					f.getName(),
        					f.getType().getSimpleName(),
        					String.valueOf(primaryNames.contains(f.getName())),
        					String.valueOf(f.isPersistent()),
        					f.getMappedName()
        					);
        */
        out.print(System.out);

        return null;
    }
}
