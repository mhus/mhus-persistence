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

import java.util.List;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.db.karaf.xdb.adb.XdbKarafUtil;
import de.mhus.db.osgi.api.xdb.XdbApi;
import de.mhus.db.osgi.api.xdb.XdbUtil;
import de.mhus.lib.core.MCollection;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.lib.xdb.XdbService;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "xdb", name = "list", description = "List all DB Services")
@Service
public class CmdList extends AbstractCmd {

    @Option(name = "-a", description = "Api Name", required = false)
    String apiName;

    @Option(name = "-c", description = "Connect", required = false)
    boolean connect;

    @Override
    public Object execute2() throws Exception {

        List<String> apis =
                apiName == null
                        ? XdbUtil.getApis()
                        : MCollection.toList(XdbKarafUtil.getApiName(getSession(), apiName));

        ConsoleTable table = new ConsoleTable(tblOpt);
        table.setHeaderValues("Service", "Schema", "DataSource", "Managed Types");

        for (String name : apis) {

            XdbApi api = XdbUtil.getApi(name);

            for (String serviceName : api.getServiceNames()) {
                if (serviceName == null) {
                    System.err.println("*** Service name is null");
                    continue;
                }
                XdbService service = api.getService(serviceName);
                if (service == null) {
                    System.err.println("*** Service is null: " + serviceName);
                    continue;
                }

                if (connect && !service.isConnected()) service.connect();

                if (service.isConnected()) {

                    int c = 0;
                    for (String typeName : service.getTypeNames()) {
                        if (c == 0) {
                            table.addRowValues(
                                    serviceName,
                                    service.getSchemaName(),
                                    service.getDataSourceName(),
                                    typeName);
                        } else {
                            table.addRowValues("", "", "", typeName);
                        }
                        c++;
                    }
                    if (c == 0) {
                        table.addRowValues(
                                serviceName,
                                service.getSchemaName(),
                                service.getDataSourceName(),
                                "");
                    }
                } else {
                    table.addRowValues(
                            serviceName, "[not connected]", service.getDataSourceName(), "");
                }
            }
        }
        /*
        DbManagerAdmin admin = AdbUtil.getAdmin();
        if (admin == null) {
        	System.out.println("Admin not found");
        	return null;
        }
        ConsoleTable table = new ConsoleTable(tblOpt);
        table.setHeaderValues("Nr","Service","Schema","DataSource","Managed Types");
        // iterate all services

        int cnt = 0;

        for ( DbManagerService service : AdbUtil.getServices(false)) {
        	if (service.isConnected()) {
        		DbManager manager = service.getManager();

        		int c = 0;
        		for (Class<? extends Persistable> type : manager.getSchema().getObjectTypes()) {
        			if (c == 0) {
        				table.addRowValues(
        						"*" + cnt,
        						service.getServiceName(),
        						manager.getSchema().getClass().getSimpleName(),
        						service.getDataSourceName(),
        						type.getSimpleName()
        					);
        			} else {
        				table.addRowValues(
        						"",
        						"",
        						"",
        						"",
        						type.getSimpleName()
        					);

        			}
        			c++;
        		}
        		if (c == 0) {
        			table.addRowValues(
        					"*" + cnt,
        					service.getServiceName(),
        					manager.getSchema().getClass().getSimpleName(),
        					service.getDataSourceName(),
        					""
        				);
        		}
        	} else {
        		table.addRowValues(
        				"*" + cnt,
        				service.getServiceName(),
        				"[not connected]",
        				service.getDataSourceName(),
        				""
        			);
        	}
        	cnt++;
        }
        */
        table.print(System.out);
        return null;
    }
}
