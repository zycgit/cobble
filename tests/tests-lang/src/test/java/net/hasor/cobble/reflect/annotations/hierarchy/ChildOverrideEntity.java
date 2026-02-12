package net.hasor.cobble.reflect.annotations.hierarchy;

/** 子类重新声明了 id 字段的 @Column，覆盖父类配置 */
@Table("override_table")
public class ChildOverrideEntity extends ParentEntity {
    @Column(name = "child_id", primary = false)
    private String id;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}
