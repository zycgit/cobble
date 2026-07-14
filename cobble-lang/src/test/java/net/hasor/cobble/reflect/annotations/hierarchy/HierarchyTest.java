package net.hasor.cobble.reflect.annotations.hierarchy;

import net.hasor.cobble.reflect.Annotation;
import net.hasor.cobble.reflect.Annotations;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * 测试 Annotations.ofClassHierarchy 方法
 * - 父类字段/方法注解合并到子类
 * - 类级注解仅保留子类自身
 * - 子类同名字段覆盖父类注解
 * - 多层继承链
 */
public class HierarchyTest {

    // ==================== ofClass (对照组：原始行为) ====================

    @Test
    public void ofClass_child_misses_parent_field_annotations() throws IOException {
        Annotations anno = Annotations.ofClass(ChildEntity.class);
        // 子类自身字段 age 有 @Column
        assertNotNull(anno.getField("age"));
        assertNotNull(anno.getField("age").getAnnotation(Column.class));
        // 父类字段 id、name、secret 在 ofClass 中不可见
        assertNull(anno.getField("id"));
        assertNull(anno.getField("name"));
        assertNull(anno.getField("secret"));
    }

    @Test
    public void ofClass_child_has_own_type_annotation() throws IOException {
        Annotations anno = Annotations.ofClass(ChildEntity.class);
        assertNotNull(anno.getAnnotation(Table.class));
        assertEquals("child_table", anno.getAnnotation(Table.class).getString("value"));
    }

    // ==================== ofClassHierarchy 基本功能 ====================

    @Test
    public void hierarchy_inherits_parent_field_annotations() throws IOException {
        Annotations anno = Annotations.ofClassHierarchy(ChildEntity.class);
        // 子类自身字段 age
        Annotations ageAnno = anno.getField("age");
        assertNotNull(ageAnno);
        Annotation ageCol = ageAnno.getAnnotation(Column.class);
        assertNotNull(ageCol);
        assertEquals("child_age", ageCol.getString("name"));

        // 父类字段 id: @Column(name="parent_id", primary=true)
        Annotations idAnno = anno.getField("id");
        assertNotNull("parent field 'id' should be visible", idAnno);
        Annotation idCol = idAnno.getAnnotation(Column.class);
        assertNotNull(idCol);
        assertEquals("parent_id", idCol.getString("name"));
        assertEquals("true", idCol.getString("primary"));

        // 父类字段 name: @Column(name="parent_name", update=false)
        Annotations nameAnno = anno.getField("name");
        assertNotNull("parent field 'name' should be visible", nameAnno);
        Annotation nameCol = nameAnno.getAnnotation(Column.class);
        assertNotNull(nameCol);
        assertEquals("parent_name", nameCol.getString("name"));
        assertEquals("false", nameCol.getString("update"));

        // 父类字段 secret: @Ignore
        Annotations secretAnno = anno.getField("secret");
        assertNotNull("parent field 'secret' should be visible", secretAnno);
        assertNotNull(secretAnno.getAnnotation(Ignore.class));
    }

    @Test
    public void hierarchy_preserves_class_annotation_only_from_child() throws IOException {
        Annotations anno = Annotations.ofClassHierarchy(ChildEntity.class);
        // 类级注解 @Table 只来自 ChildEntity 本身
        Annotation table = anno.getAnnotation(Table.class);
        assertNotNull(table);
        assertEquals("child_table", table.getString("value"));
    }

    @Test
    public void hierarchy_parent_has_no_table_annotation_not_leaked() throws IOException {
        // ParentEntity 没有 @Table，ChildEntity 有
        Annotations parentAnno = Annotations.ofClassHierarchy(ParentEntity.class);
        assertNull(parentAnno.getAnnotation(Table.class));
        // parentAnno 自身字段应该可见
        assertNotNull(parentAnno.getField("id"));
        assertNotNull(parentAnno.getField("name"));
    }

    // ==================== 子类覆盖 ====================

    @Test
    public void hierarchy_child_override_takes_precedence() throws IOException {
        Annotations anno = Annotations.ofClassHierarchy(ChildOverrideEntity.class);
        // 子类自己声明了 id 字段的 @Column(name="child_id", primary=false)
        Annotations idAnno = anno.getField("id");
        assertNotNull(idAnno);
        Annotation idCol = idAnno.getAnnotation(Column.class);
        assertNotNull(idCol);
        // 子类先加载，getAnnotation 取 get(0)，应该是子类的配置
        assertEquals("child_id", idCol.getString("name"));
        assertEquals("false", idCol.getString("primary"));
    }

    // ==================== 多层继承链 ====================

    @Test
    public void hierarchy_three_level_chain() throws IOException {
        Annotations anno = Annotations.ofClassHierarchy(LeafEntity.class);
        // LeafEntity 自身字段 value
        Annotations valueAnno = anno.getField("value");
        assertNotNull(valueAnno);
        assertEquals("leaf_value", valueAnno.getAnnotation(Column.class).getString("name"));

        // MiddleEntity 字段 label
        Annotations labelAnno = anno.getField("label");
        assertNotNull("middle class field 'label' should be visible", labelAnno);
        assertEquals("mid_label", labelAnno.getAnnotation(Column.class).getString("name"));

        // GrandParentEntity 字段 code
        Annotations codeAnno = anno.getField("code");
        assertNotNull("grandparent field 'code' should be visible", codeAnno);
        Annotation codeCol = codeAnno.getAnnotation(Column.class);
        assertEquals("gp_code", codeCol.getString("name"));
        assertEquals("true", codeCol.getString("primary"));
    }

    @Test
    public void hierarchy_three_level_class_anno_only_from_leaf() throws IOException {
        Annotations anno = Annotations.ofClassHierarchy(LeafEntity.class);
        // @Table 只在 LeafEntity 上，不会被父类影响
        assertNotNull(anno.getAnnotation(Table.class));
        assertEquals("leaf_table", anno.getAnnotation(Table.class).getString("value"));
    }

    // ==================== 边界情况 ====================

    @Test
    public void hierarchy_no_parent_same_as_ofClass() throws IOException {
        // ParentEntity 没有父类（除了 Object），应和 ofClass 行为一致
        Annotations h = Annotations.ofClassHierarchy(ParentEntity.class);
        Annotations o = Annotations.ofClass(ParentEntity.class);

        // 字段 id 应该都存在
        assertNotNull(h.getField("id"));
        assertNotNull(o.getField("id"));

        // 注解值相同
        assertEquals(                                                   //
                h.getField("id").getAnnotation(Column.class).getString("name"),  //
                o.getField("id").getAnnotation(Column.class).getString("name")   //
        );
    }

    @Test
    public void hierarchy_unmapped_parent_field_no_annotation() throws IOException {
        Annotations anno = Annotations.ofClassHierarchy(ChildEntity.class);
        // 父类 unmapped 字段没有任何注解，fieldData 中不存在
        Annotations unmappedAnno = anno.getField("unmapped");
        // 没有注解的字段，ASM 不会为它生成 visitField 回调中的 putFieldData
        // 所以 getField 返回 null 是正常的
        // 只是确保不会因为 hierarchy 而引入奇怪的东西
        if (unmappedAnno != null) {
            assertNull(unmappedAnno.getAnnotation(Column.class));
        }
    }

    @Test
    public void hierarchy_object_class_is_skipped() throws IOException {
        // 确保 Object.class 就算被遍历也不会引入额外注解
        Annotations anno = Annotations.ofClassHierarchy(ParentEntity.class);
        // ParentEntity 有4个字段 (id, name, secret, unmapped)，其中3个有注解
        assertNotNull(anno.getField("id"));
        assertNotNull(anno.getField("name"));
        assertNotNull(anno.getField("secret"));
    }
}
