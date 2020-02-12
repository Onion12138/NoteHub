package com.ecnu.onion.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author onion
 * @date 2020/2/12 -11:53 上午
 */
@Data
@NoArgsConstructor
public class MindMap {
    private boolean leaf;
    private List<MindMap> children;
    private String id;
    private String noteId;

    public MindMap(String id) {
        this.id = id;
        children = new ArrayList<>();
        leaf = false;
    }

    public MindMap(String id, String noteId) {
        this.id = id;
        this.noteId = noteId;
        leaf = true;
    }

    public void addComponent(MindMap component) {
        if (leaf) {
            throw new RuntimeException("不是目录");
        }
        children.add(component);
    }
}
