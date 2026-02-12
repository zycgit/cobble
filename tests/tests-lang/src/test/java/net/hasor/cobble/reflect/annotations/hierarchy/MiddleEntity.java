package net.hasor.cobble.reflect.annotations.hierarchy;

public class MiddleEntity extends GrandParentEntity {
    @Column(name = "mid_label")
    private String label;

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }
}
