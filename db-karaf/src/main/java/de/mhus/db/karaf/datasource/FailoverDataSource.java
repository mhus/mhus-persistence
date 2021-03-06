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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;

import javax.sql.DataSource;

import de.mhus.osgi.api.util.DataSourceUtil;

public class FailoverDataSource extends AbstractDataSource {

    private String source;
    private String realList = "";
    private LinkedList<DataSource> list;
    private int current = 0;

    @Override
    public DataSource getDataSource() throws SQLFeatureNotSupportedException {

        synchronized (this) {
            if (list == null) {
                current = 0;
                list = new LinkedList<DataSource>();
                realList = "";
                for (String name : source.split(",")) {
                    DataSource dataSource = DataSourceUtil.getDataSource(name);
                    if (dataSource != null) {
                        list.add(dataSource);
                        if (realList.length() > 0) realList = realList + ",";
                        realList = realList + name;
                    }
                }
                if (list.size() == 0) {
                    list = null;
                    instanceName = "failover:" + source;
                    return null;
                }
                instanceName = "failover:" + realList + " [" + current + "]";
            }
        }

        return list.get(current);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        SQLException lastException = null;
        while (list != null) {
            try {
                return getDataSource().getConnection(username, password);
            } catch (SQLException e) {
                if (list.isEmpty()) throw e;
                // move first connection to the end
                doDisconnect();
                lastException = e;
            }
        }
        if (lastException != null) throw lastException;
        throw new SQLException("no DataSource found", lastException);
    }

    @Override
    public void doDisconnect() {
        if (list == null) {
            instanceName = "failover:" + source;
            return;
        }
        // do a failover
        // current = (current+1) % list.size();
        current = current + 1;
        if (current >= list.size()) list = null; // disconnect
        instanceName = "failover:" + realList + " [" + current + "]";
    }

    @Override
    public boolean isInstanceConnected() {
        return list != null;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
        instanceName = "failover:" + source;
    }
}
