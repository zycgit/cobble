/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.tree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.cobble.BeanUtils;
import net.hasor.cobble.CollectionUtils;
import org.junit.Test;

public class PidTreeTest {
    private TreeDTO newProject(int id, Integer pid, String name) {
        TreeDTO project = new TreeDTO();
        project.setId(id);
        project.setPid(pid);
        project.setName(name);
        return project;
    }

    @Test
    public void pidTreeDTO_TestCase() {
        List<TreeDTO> dtoList = new ArrayList<>();
        dtoList.add(newProject(1, null, "project 1"));
        dtoList.add(newProject(2, 1, "project 2"));
        dtoList.add(newProject(3, 2, "project 3"));
        dtoList.add(newProject(4, null, "project 4"));
        dtoList.add(newProject(5, 4, "project 5"));

        List<TreeDTO> root = CollectionUtils.toTree(dtoList, TreeDTO::getSub, TreeDTO::getId, TreeDTO::getPid);
        assert root.size() == 2;
        assert root.get(0).getId() == 1;
        assert root.get(0).getSub().size() == 1;
        assert root.get(0).getSub().get(0).getId() == 2;
        assert root.get(0).getSub().get(0).getSub().size() == 1;
        assert root.get(0).getSub().get(0).getSub().get(0).getId() == 3;
        assert root.get(1).getId() == 4;
        assert root.get(1).getSub().size() == 1;
        assert root.get(1).getSub().get(0).getId() == 5;
    }

    @Test
    public void pidTreeMap_TestCase() {
        List<TreeDTO> dtoList = new ArrayList<>();
        dtoList.add(newProject(1, null, "project 1"));
        dtoList.add(newProject(2, 1, "project 2"));
        dtoList.add(newProject(3, 2, "project 3"));
        dtoList.add(newProject(4, null, "project 4"));
        dtoList.add(newProject(5, 4, "project 5"));

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (TreeDTO dto : dtoList) {
            Map<String, Object> ent = new HashMap<>();
            BeanUtils.copyProperties(ent, dto);
            mapList.add(ent);
        }

        List<Map<String, Object>> root = CollectionUtils.toTree(mapList, "sub", "id", "pid");
        assert root.size() == 2;
        assert root.get(0).get("id").equals(1);
        assert ((List) root.get(0).get("sub")).size() == 1;
        assert ((Map) ((List) root.get(0).get("sub")).get(0)).get("id").equals(2);
        assert ((List) ((Map) ((List) root.get(0).get("sub")).get(0)).get("sub")).size() == 1;
        assert ((Map) ((List) ((Map) ((List) root.get(0).get("sub")).get(0)).get("sub")).get(0)).get("id").equals(3);
        assert root.get(1).get("id").equals(4);
        assert ((List) root.get(1).get("sub")).size() == 1;
        assert ((Map) ((List) root.get(1).get("sub")).get(0)).get("id").equals(5);
    }
}
