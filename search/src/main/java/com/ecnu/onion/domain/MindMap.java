package com.ecnu.onion.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author onion
 * @date 2020/3/9 -11:14 下午
 */
@Data
@NoArgsConstructor
public class MindMap {
    private List<com.ecnu.onion.domain.MindMap> children;
    private String label;
    private String value;

    public MindMap(String label) {
        this.label = label;
        this.value = label;
        children = new ArrayList<>();
    }

    public MindMap(String label, String value, boolean hasChildren) {
        this.label = label;
        this.value = value;
        if (hasChildren) {
            children = new ArrayList<>();
        }
    }

    public void addComponent(com.ecnu.onion.domain.MindMap component) {
        children.add(component);
    }
}
