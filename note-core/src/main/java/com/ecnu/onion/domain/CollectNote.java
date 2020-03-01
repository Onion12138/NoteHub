package com.ecnu.onion.domain;

import lombok.Data;

/**
 * @author onion
 * @date 2020/2/12 -11:48 上午
 */
@Data
public class CollectNote {
    private String noteId;
    private String tag;
    private String description;
    @Override
    public boolean equals(Object o){
        if (o instanceof CollectNote) {
            CollectNote note = (CollectNote) o;
            return this.noteId.equals(note.noteId);
        }else {
            return false;
        }
    };
    @Override
    public int hashCode() {
        return noteId.hashCode();
    }
}
