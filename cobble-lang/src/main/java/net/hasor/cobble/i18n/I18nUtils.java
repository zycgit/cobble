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
package net.hasor.cobble.i18n;
import net.hasor.cobble.ClassUtils;
import net.hasor.cobble.ResourcesUtils;
import net.hasor.cobble.StringUtils;
import net.hasor.cobble.ref.LinkedCaseInsensitiveMap;
import net.hasor.cobble.text.token.GenericTokenParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 国际化资源文件加载器
 * @author 赵永春 (zyc@hasor.net)
 */
public class I18nUtils {

    public static final  I18nUtils           DEFAULT     = new I18nUtils();
    private static final Map<String, Locale> LOCAL_CACHE = new LinkedCaseInsensitiveMap<>();

    static {
        for (Locale locale : Locale.getAvailableLocales()) {
            String i18nKey = locale.getLanguage() + "_" + locale.getCountry();
            LOCAL_CACHE.put(i18nKey, locale);
        }
    }

    public static I18nUtils initI18n(String... i18nResources) throws IOException {
        I18nUtils utils = new I18nUtils();
        utils.loadResources(i18nResources);
        return utils;
    }

    public static I18nUtils initI18n(ClassLoader resourceLoader, String... i18nResources) throws IOException {
        I18nUtils utils = new I18nUtils();
        utils.loadResources(resourceLoader, i18nResources);
        return utils;
    }

    /** 根据 i8n Key 获取 Locale，i18nKey 格式为："语言_国家"; 可以大小写不敏感 */
    public static Locale getLocale(String i18nKey) {
        return LOCAL_CACHE.get(i18nKey);
    }

    /** 根据 语言和国家代码获取 Locale，可以大小写不敏感 */
    public static Locale getLocale(String language, String country) {
        return LOCAL_CACHE.computeIfAbsent(language + "_" + country, s -> new Locale(language, country));
    }

    /** 根据 语言和国家代码 生成 i18nKey */
    public static String toI18nKey(String language, String country) {
        if (StringUtils.isNotBlank(language) && StringUtils.isNotBlank(country)) {
            return language + "_" + country;
        } else if (StringUtils.isNotBlank(country)) {
            return country;
        } else {
            return language;
        }
    }

    /** 根据 Locale 生成 i18nKey */
    public static String toI18nKey(Locale locale) {
        if (locale == null) {
            return null;
        }

        if (StringUtils.isNotBlank(locale.getLanguage()) && StringUtils.isNotBlank(locale.getCountry())) {
            return locale.getLanguage() + "_" + locale.getCountry();
        } else if (StringUtils.isNotBlank(locale.getCountry())) {
            return locale.getCountry();
        } else {
            return locale.getLanguage();
        }
    }

    public static interface I18nMessageSource {
        String getMessage(String code, Object[] args, Locale locale);
    }

    private static class I18nMessageSourceImpl implements I18nMessageSource {
        private final Map<String, Map<Locale, String>> i18nDictionary = new ConcurrentHashMap<>();

        @Override
        public String getMessage(String code, Object[] args, Locale locale) {
            if (locale == null) {
                locale = Locale.getDefault();
            }

            Map<Locale, String> localeMap = this.i18nDictionary.get(code);
            if (localeMap == null || !localeMap.containsKey(locale)) {
                return null;
            }
            return localeMap.get(locale);
        }

        public void putDictionary(String code, Locale locale, String message) {
            if (locale == null) {
                locale = Locale.getDefault();
            }

            if (!this.i18nDictionary.containsKey(code)) {
                this.i18nDictionary.put(code, new ConcurrentHashMap<>());
            }
            this.i18nDictionary.get(code).put(locale, message);
        }
    }

    private static class VariablesSourceImpl extends ConcurrentHashMap<String, String> implements Function<String, String> {

        @Override
        public String apply(String s) {
            return super.getOrDefault(s, s);
        }
    }

    private       String                   defaultI18nKey;
    private final I18nMessageSource        messageSource;
    private final Function<String, String> variablesSource;
    private final Map<String, ClassLoader> i18nSource;
    private final Set<String>              i18nLoaded;

    private I18nUtils() {
        this.defaultI18nKey = toI18nKey(Locale.getDefault());
        this.messageSource = new I18nMessageSourceImpl();
        this.variablesSource = new VariablesSourceImpl();
        this.i18nSource = new LinkedHashMap<>();
        this.i18nLoaded = new LinkedHashSet<>();
    }

    public I18nUtils(I18nMessageSource messageSource) {
        this.defaultI18nKey = toI18nKey(Locale.getDefault());
        this.messageSource = Objects.requireNonNull(messageSource, "messageSource is null.");
        this.variablesSource = new VariablesSourceImpl();
        this.i18nSource = new LinkedHashMap<>();
        this.i18nLoaded = new LinkedHashSet<>();
    }

    public I18nUtils(I18nMessageSource messageSource, Function<String, String> variablesSource) {
        this.defaultI18nKey = toI18nKey(Locale.getDefault());
        this.messageSource = Objects.requireNonNull(messageSource, "messageSource is null.");
        this.variablesSource = variablesSource == null ? new VariablesSourceImpl() : variablesSource;
        this.i18nSource = new LinkedHashMap<>();
        this.i18nLoaded = new LinkedHashSet<>();
    }

    public String getDefaultI18nKey() {
        return defaultI18nKey;
    }

    public void setDefaultI18nKey(String defaultI18nKey) {
        this.defaultI18nKey = defaultI18nKey;
    }

    public void setDefaultI18nKey(Locale defaultLocale) {
        this.defaultI18nKey = toI18nKey(defaultLocale);
    }

    protected void loadResources(String... i18nResources) throws IOException {
        ClassLoader classLoader = ClassUtils.getClassLoader(Thread.currentThread().getContextClassLoader());
        loadResources(classLoader, i18nResources);
    }

    protected void loadResources(ClassLoader resourceLoader, String... i18nResources) throws IOException {
        if (!(this.messageSource instanceof I18nMessageSourceImpl)) {
            throw new UnsupportedOperationException("I18nMessageSource is external.");
        }

        for (String i18n : i18nResources) {
            if (!this.i18nSource.containsKey(i18n)) {
                resourceLoader = resourceLoader == null ? ClassUtils.getClassLoader(Thread.currentThread().getContextClassLoader()) : resourceLoader;
                this.i18nSource.put(i18n, resourceLoader);
            }
        }
    }

    public void loadResources(Class<?>... i18nResources) throws IOException {
        Set<Class<?>> loaded = new HashSet<>();
        for (Class<?> i18nType : i18nResources) {
            // deep parents
            List<Class<?>> interfaces = ClassUtils.getAllInterfaces(i18nType);
            List<Class<?>> superclasses = ClassUtils.getAllSuperclasses(i18nType);
            Collections.reverse(interfaces);
            Collections.reverse(superclasses);

            // all load type
            List<Class<?>> loadTypes = new ArrayList<>();
            loadTypes.addAll(interfaces);
            loadTypes.addAll(superclasses);
            loadTypes.add(i18nType);

            for (Class<?> type : loadTypes) {
                if (loaded.contains(type)) {
                    continue;
                }

                loaded.add(type);
                I18nResource i18nResource = type.getAnnotation(I18nResource.class);
                if (i18nResource != null) {
                    this.loadResources(type.getClassLoader(), i18nResource.value());
                }
            }
        }
    }

    /** 添加可替换变量 */
    public void addVariables(String varName, String varValue) {
        if (this.variablesSource instanceof VariablesSourceImpl) {
            ((VariablesSourceImpl) this.variablesSource).put(varName, varValue);
        } else {
            throw new UnsupportedOperationException("variablesSource is external.");
        }
    }

    public void putVariables(Map<String, String> variables) {
        if (variables != null && this.variablesSource instanceof VariablesSourceImpl) {
            ((VariablesSourceImpl) this.variablesSource).putAll(variables);
        } else {
            throw new UnsupportedOperationException("variablesSource is external.");
        }
    }

    /** 获取 i18n 文本 */
    public String getMessage(String code) {
        return getMessage(code, null, this.defaultI18nKey);
    }

    /** 获取 i18n 文本 */
    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, this.defaultI18nKey);
    }

    /** 获取 i18n 文本 */
    public String getMessage(String code, Object[] args, String i18nLocal) {
        if (StringUtils.isBlank(code)) {
            return code;
        }

        Locale locale;
        if (LOCAL_CACHE.containsKey(i18nLocal)) {
            locale = LOCAL_CACHE.get(i18nLocal);
        } else {
            locale = Locale.getDefault();
        }
        return this.getMessage(code, args, locale);
    }

    /** 获取 i18n 文本 */
    public String getMessage(String code, Object[] args, Locale locale) {
        String i18nKey = toI18nKey(locale);

        if (!this.i18nLoaded.contains(i18nKey)) {
            synchronized (this) {
                if (!this.i18nLoaded.contains(i18nKey)) {
                    for (String i18nResourcePath : this.i18nSource.keySet()) {
                        String i18nResource = i18nResourcePath + "_" + i18nKey + ".properties";
                        ClassLoader i18nLoader = this.i18nSource.get(i18nResourcePath);

                        try (InputStream stream = ResourcesUtils.getResourceAsStream(i18nLoader, i18nResource)) {
                            if (stream != null) {
                                Properties properties = new Properties();
                                properties.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
                                properties.forEach((key, value) -> {
                                    ((I18nMessageSourceImpl) this.messageSource).putDictionary(key.toString(), locale, value.toString());
                                });
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    this.i18nLoaded.add(i18nKey);
                }
            }
        }

        String message = this.messageSource.getMessage(code, args, locale);
        if (message == null) {
            return code;
        }

        message = resolveMessageArgs(message);
        if (args == null || args.length == 0) {
            return message;
        }

        return MessageFormat.format(message, args);
    }

    private String resolveMessageArgs(String msg) {
        return new GenericTokenParser(new String[] { "${" }, "}", (builder, openToken, closeToken, content) -> {
            String varKey = content;
            String varDefault = "";
            int defaultIndexOf = content.indexOf(":");
            if (defaultIndexOf != -1) {
                varDefault = content.substring(defaultIndexOf + 1);
                varKey = content.substring(0, defaultIndexOf);
            }

            String var = resolveArg(varKey);
            if (StringUtils.isBlank(var) && StringUtils.isNotBlank(varDefault)) {
                var = varDefault;
            }

            if (varKey.equalsIgnoreCase(var)) {
                return varKey;
            } else {
                return var;
            }
        }).parse(msg);
    }

    protected String resolveArg(String argName) {
        if (this.variablesSource != null) {
            return this.variablesSource.apply(argName);
        } else {
            return argName;
        }
    }
}