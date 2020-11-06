/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.wallet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Methods to load configuration files.
 *
 * @since 2019-11-06
 */
public final class ConfigUtil {
    /**
     * Configuration filename.
     */
    private final static String SYSTEM_CONFIG = "release.config.properties";

    /**
     * Data folder.
     */
    private final static String DATA = "data/";

    /**
     * Singleton pattern instance.
     */
    private static ConfigUtil config;

    private Map<String, String> params;

    private ConfigUtil() {
        load();
    }

    /**
     * Singleton pattern implementation.
     *
     * @return the singleton instance.
     */
    public static ConfigUtil instants() {
        if (null == config) {
            config = new ConfigUtil();
        }
        return config;
    }

    /**
     * Load a configuration file.
     */
    private void load() {
        if (null == params) {
            params = new HashMap<>();
        } else {
            params.clear();
        }
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SYSTEM_CONFIG);
        try {
            properties.load(inputStream);
            for (Object key : properties.keySet()) {
                if (null != key && !"".equals(key.toString())) {
                    String value = properties.getProperty(key.toString());
                    params.put(key.toString().trim(), value.trim());
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("load " + SYSTEM_CONFIG + " failed.");
        }
    }

    /**
     * Get a specific value in a configuration file by its key.
     *
     * @param key the key to the value.
     * @return the value string.
     */
    public String getValue(String key) {
        return params.get(key);
    }

    /**
     * Read a file in the test/resources/data folder.
     *
     * @param fileName file name
     * @return the value string.
     */
    public static String readFile(String fileName) {
        InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream(DATA + fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File " + fileName + "not found.");
        }
        Reader reader = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Read file " + fileName + " failed.");
        } finally {
            try {
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
                if (null != reader) {
                    reader.close();
                }
                inputStream.close();
            } catch (IOException e) {
                throw new IllegalArgumentException("File " + fileName + " close failed.");
            }
        }
        return sb.toString();
    }
}
