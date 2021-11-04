/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Xml 工具类
 * @version 2021-10-04
 * @author 赵永春 (zyc@hasor.net)
 */
public class XmlUtils {

    private static DocumentBuilderFactory BUILDER_FACTORY_NONE_VALID;

    public static DocumentBuilderFactory getDocumentBuilderWithNoValid() {
        if (BUILDER_FACTORY_NONE_VALID == null) {
            BUILDER_FACTORY_NONE_VALID = DocumentBuilderFactory.newInstance();
            BUILDER_FACTORY_NONE_VALID.setValidating(false);
            BUILDER_FACTORY_NONE_VALID.setNamespaceAware(true);
            try {
                BUILDER_FACTORY_NONE_VALID.setFeature("http://xml.org/sax/features/namespaces", false);
                BUILDER_FACTORY_NONE_VALID.setFeature("http://xml.org/sax/features/validation", false);
                BUILDER_FACTORY_NONE_VALID.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
                BUILDER_FACTORY_NONE_VALID.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            } catch (Exception e) { /* ignore */}
        }
        return BUILDER_FACTORY_NONE_VALID;
    }

}
