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
package de.mhus.lib.adb.query;

import de.mhus.lib.core.parser.AttributeMap;

public class ALimit extends AOperation {

    private int limit;
    private int offset;

    public ALimit(int limit) {
        this(0, limit);
    }

    public ALimit(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public void getAttributes(AQuery<?> query, AttributeMap map) {}

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }
}
