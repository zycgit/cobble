/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.reflect.annotations.hierarchy;

public class ParentEntity {
    @Column(name = "parent_id", primary = true)
    private String id;

    @Column(name = "parent_name", update = false)
    private String name;

    @Ignore
    private String secret;

    private String unmapped;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUnmapped() {
        return unmapped;
    }

    public void setUnmapped(String unmapped) {
        this.unmapped = unmapped;
    }
}
