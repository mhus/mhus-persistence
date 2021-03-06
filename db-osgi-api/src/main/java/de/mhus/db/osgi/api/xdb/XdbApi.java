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
package de.mhus.db.osgi.api.xdb;

import java.util.List;

import de.mhus.lib.basics.Adaptable;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.xdb.XdbService;
import de.mhus.lib.xdb.XdbType;

/**
 * Basic interface to handle multiple types of databases with the same framework.
 *
 * @author mikehummel
 */
public interface XdbApi extends Adaptable {

    XdbService getService(String serviceName) throws NotFoundException;

    <T> XdbType<T> getType(String serviceName, String typeName) throws NotFoundException;

    List<String> getServiceNames();
}
