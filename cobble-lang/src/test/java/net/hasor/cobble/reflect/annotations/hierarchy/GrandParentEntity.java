package net.hasor.cobble.reflect.annotations.hierarchy;

public class GrandParentEntity {
    @Column(name = "gp_code", primary = true)
    private String code;

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }
}
