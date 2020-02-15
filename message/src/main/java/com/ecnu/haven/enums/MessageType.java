package com.ecnu.haven.enums;

/**
 * @author HavenTong
 * @date 2020/2/15 11:21 上午
 * 消息类型的枚举类型
 */
public enum MessageType {
    /**
     * 创建小组的消息
     */
    CREATE_GROUP,
    /**
     * 组内分享笔记的消息
     */
    SHARE_NOTE,
    /**
     * 被评论的消息
     */
    COMMENT,
    /**
     * 评论回复的消息
     */
    COMMENT_REPLY,
    /**
     * 关注作者更新的消息
     */
    FOLLOW_UPDATE
}
