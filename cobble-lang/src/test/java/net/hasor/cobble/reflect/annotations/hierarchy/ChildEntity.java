package net.hasor.cobble.reflect.annotations.hierarchy;

@Table("child_table")
public class ChildEntity extends ParentEntity {
    @Column(name = "child_age")
    private int age;

    public int getAge() { return age; }

    public void setAge(int age) { this.age = age; }
}
