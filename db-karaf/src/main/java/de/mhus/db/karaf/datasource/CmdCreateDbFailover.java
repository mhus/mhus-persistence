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
package de.mhus.db.karaf.datasource;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.osgi.api.karaf.AbstractCmd;
import de.mhus.osgi.api.util.DataSourceUtil;
import de.mhus.osgi.api.util.TemplateUtils;

@Command(scope = "jdbc", name = "createdbfailover", description = "Create DB Failover DataSource")
@Service
public class CmdCreateDbFailover extends AbstractCmd {

    @Option(
            name = "-o",
            aliases = {"--online"},
            description = "Create the datasource online and not a blueprint",
            required = false,
            multiValued = false)
    boolean online;

    @Argument(
            index = 0,
            name = "sources",
            required = true,
            description = "Source Datasources, separated by comma",
            multiValued = false)
    String sources;

    @Argument(
            index = 1,
            name = "target",
            required = true,
            description = "New Pooling Datasource",
            multiValued = false)
    String target;

    @Override
    public Object execute2() throws Exception {

        if (online) {

            FailoverDataSource dataSource = new FailoverDataSource();
            dataSource.setSource(sources);

            DataSourceUtil.registerDataSource(dataSource, target);

        } else {

            File karafBase = new File(System.getProperty("karaf.base"));
            File deployFolder = new File(karafBase, "deploy");
            File outFile = new File(deployFolder, "datasource-failover_" + target + ".xml");

            HashMap<String, Object> properties = new HashMap<>();
            properties.put("name", target);
            properties.put("source", sources);
            String templateFile = "datasource-failover.xml";
            InputStream is = this.getClass().getResourceAsStream(templateFile);
            if (is == null) {
                throw new IllegalArgumentException(
                        "Template resource " + templateFile + " doesn't exist");
            }
            TemplateUtils.createFromTemplate(outFile, is, properties);
        }

        return null;
    }
}
