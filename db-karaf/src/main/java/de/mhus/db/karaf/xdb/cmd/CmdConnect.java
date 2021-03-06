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

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.db.karaf.xdb.adb.XdbKarafUtil;
import de.mhus.db.osgi.api.xdb.XdbApi;
import de.mhus.db.osgi.api.xdb.XdbUtil;
import de.mhus.lib.xdb.XdbService;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "xdb", name = "connect", description = "Connect ADB DataSource")
@Service
public class CmdConnect extends AbstractCmd {

    @Argument(
            index = 0,
            name = "service",
            required = true,
            description = "Service Class",
            multiValued = false)
    String serviceName;

    @Option(
            name = "-u",
            aliases = "--update",
            description = "Causes the driver to reconnect to the datasource",
            required = false)
    boolean update = false;

    @Option(
            name = "-c",
            aliases = "--cleanup",
            description =
                    "Cleanup unised table field and indexes - this can delete additional data",
            required = false)
    boolean cleanup = false;

    @Option(name = "-a", description = "Api Name", required = false)
    String apiName;

    @Override
    public Object execute2() throws Exception {

        apiName = XdbKarafUtil.getApiName(getSession(), apiName);
        serviceName = XdbKarafUtil.getServiceName(getSession(), serviceName);

        XdbApi api = XdbUtil.getApi(apiName);

        XdbService service = api.getService(serviceName);

        if (update || cleanup) service.updateSchema(cleanup);
        else service.connect(); // this call will touch the service and connect to the database
        System.out.println("OK");

        return null;

        /*
        DbManagerService service = AdbUtil.getService(serviceName);
        if (service != null) {
        	if (update || cleanup)
        		service.updateManager(cleanup);
        	else
        		service.getManager(); // this call will touch the service and connect to the database
        	System.out.println("OK");
        } else {
        	System.out.println("Not found");
        }
        */
    }
}
