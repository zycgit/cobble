package net.hasor.cobble.reflect.annotations.hierarchy;

@Table("leaf_table")
public class LeafEntity extends MiddleEntity {
    @Column(name = "leaf_value")
    private int value;

    public int getValue() { return value; }

    public void setValue(int value) { this.value = value; }
}
