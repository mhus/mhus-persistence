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
package de.mhus.db.karaf.xdb.adb;

/**
 * Singleton Service Interface
 *
 * <p>Tooling API for karaf commands. xdb:use is the manager of this api. It provides the current
 * used api and services.
 *
 * @author mikehummel
 */
public interface XdbKarafApi {

    void setApi(String api);

    String getService();

    void setService(String service);

    String getDatasource();

    void setDatasource(String datasource);

    String getApi();

    void load();

    void save();
}
