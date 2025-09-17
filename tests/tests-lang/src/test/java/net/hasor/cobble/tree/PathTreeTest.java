package net.hasor.cobble.tree;
import net.hasor.cobble.BeanUtils;
import net.hasor.cobble.CollectionUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathTreeTest {

    private TreeDTO newProject(int id, String name, String feature) {
        TreeDTO project = new TreeDTO();
        project.setId(id);
        project.setName(name);
        project.setFeature(feature);
        return project;
    }

    @Test
    public void pathTreeDTO_TestCase() {
        //  | ID | PATH | NAME       | ... |
        //  | 1  | /b   | TreeNode 1 | ... |
        //  | 2  | /b/c | TreeNode 2 | ... |
        //  | 3  | /b/a | TreeNode 3 | ... |
        //  | 4  | /a   | TreeNode 4 | ... |
        //  | 5  | /a/b | TreeNode 5 | ... |
        //  | 6  | NULL | TreeNode 6 | ... |
        List<TreeDTO> dtoList = new ArrayList<>();
        dtoList.add(newProject(1, "/b", "project 1"));
        dtoList.add(newProject(2, "/b/c", "project 2"));
        dtoList.add(newProject(3, "/b/a", "project 3"));
        dtoList.add(newProject(4, "/a", "project 4"));
        dtoList.add(newProject(5, "/a/b", "project 5"));
        dtoList.add(newProject(6, null, "project 6"));

        List<TreeDTO> root = CollectionUtils.toTree(dtoList, TreeDTO::getSub, TreeDTO::getName);
        assert root.size() == 3;
        assert root.get(0).getId() == 1;
        assert root.get(0).getSub().size() == 2;
        assert root.get(0).getSub().get(0).getId() == 2;
        assert root.get(0).getSub().get(1).getId() == 3;
        assert root.get(1).getId() == 4;
        assert root.get(1).getSub().get(0).getId() == 5;
        assert root.get(2).getId() == 6;
    }

    @Test
    public void pathTreeMap_TestCase() {
        //  | ID | PATH | NAME       | ... |
        //  | 1  | /b   | TreeNode 1 | ... |
        //  | 2  | /b/c | TreeNode 2 | ... |
        //  | 3  | /b/a | TreeNode 3 | ... |
        //  | 4  | /a   | TreeNode 4 | ... |
        //  | 5  | /a/b | TreeNode 5 | ... |
        //  | 6  | NULL | TreeNode 6 | ... |
        List<TreeDTO> dtoList = new ArrayList<>();
        dtoList.add(newProject(1, "/b", "project 1"));
        dtoList.add(newProject(2, "/b/c", "project 2"));
        dtoList.add(newProject(3, "/b/a", "project 3"));
        dtoList.add(newProject(4, "/a", "project 4"));
        dtoList.add(newProject(5, "/a/b", "project 5"));
        dtoList.add(newProject(6, null, "project 6"));

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (TreeDTO dto : dtoList) {
            Map<String, Object> ent = new HashMap<>();
            BeanUtils.copyProperties(ent, dto);
            mapList.add(ent);
        }

        List<Map<String, Object>> root = CollectionUtils.toTree(mapList, "sub", "name");
        assert root.size() == 3;
        assert root.get(0).get("id").equals(1);
        assert ((List) root.get(0).get("sub")).size() == 2;
        assert ((Map) ((List) root.get(0).get("sub")).get(0)).get("id").equals(2);
        assert ((Map) ((List) root.get(0).get("sub")).get(1)).get("id").equals(3);
        assert ((Map) root.get(1)).get("id").equals(4);
        assert ((Map) ((List) root.get(1).get("sub")).get(0)).get("id").equals(5);
        assert ((Map) root.get(2)).get("id").equals(6);
    }
}
