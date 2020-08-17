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
package de.mhus.lib.sql.parser;

import java.io.IOException;

import de.mhus.lib.core.parser.ConstantParsingPart;
import de.mhus.lib.core.parser.ParseException;
import de.mhus.lib.core.parser.ParseReader;

public class ConstWordPart extends ConstantParsingPart {

    public ConstWordPart(ICompiler compiler) {}

    @Override
    public boolean parse(char c, ParseReader str) throws ParseException, IOException {

        if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_') {
            buffer.append(c);
            str.consume();
            return true;
        }
        return false;
    }
}
