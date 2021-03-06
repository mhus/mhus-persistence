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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import javax.sql.DataSource;

import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.logging.FileLogger;
import de.mhus.osgi.api.util.DataSourceUtil;

public class TraceDataSource extends AbstractDataSource {

    private String source;
    private DataSource dataSource;
    private Log log = Log.getLog(TraceDataSource.class); // TODO change !
    private boolean trace;
    private String traceFile = "";

    @Override
    public DataSource getDataSource() throws SQLFeatureNotSupportedException {

        synchronized (this) {
            if (dataSource == null) {
                dataSource = DataSourceUtil.getDataSource(source);
            }
        }

        return dataSource;
    }

    @Override
    public void doDisconnect() {
        dataSource = null;
    }

    @Override
    public boolean isInstanceConnected() {
        return dataSource != null;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
        instanceName = "trace(" + isTrace() + "):" + source;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new TracedConnection(getDataSource().getConnection(), this);
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
        setSource(source);
    }

    public void setTraceFile(String file) {
        if (MString.isEmptyTrim(file)) log = Log.getLog(TraceDataSource.class);
        else log = new FileLogger("", new File(file));
        traceFile = file;
    }

    public boolean isTrace() {
        return log != null && trace;
    }

    public long startTrace(String... attr) {
        if (isTrace()) {
            return System.currentTimeMillis();
        }
        return 0;
    }

    public void stopTrace(long time, String... attr) {
        if (time == 0) return;
        long delta = System.currentTimeMillis() - time;
        log.i(source, delta, attr);
    }

    public String getTraceFile() {
        return traceFile;
    }
}
