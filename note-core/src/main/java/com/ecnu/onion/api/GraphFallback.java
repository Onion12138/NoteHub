package com.ecnu.onion.api;

import com.ecnu.onion.vo.BaseResponseVO;

/**
 * @author onion
 * @date 2020/3/1 -3:31 下午
 */
public class GraphFallback implements GraphAPI {
    @Override
    public BaseResponseVO addForkRelation(String email, String noteId) {
        return BaseResponseVO.error("添加fork关系出错");
    }

    @Override
    public BaseResponseVO addViewRelation(String email, String noteId) {
        return BaseResponseVO.error("添加view关系出错");
    }

    @Override
    public BaseResponseVO addStarRelation(String email, String noteId) {
        return BaseResponseVO.error("添加star关系出错");
    }

    @Override
    public BaseResponseVO addHateRelation(String email, String noteId) {
        return BaseResponseVO.error("添加hate关系出错");
    }

    @Override
    public BaseResponseVO addCollectRelation(String email, String noteId) {
        return BaseResponseVO.error("添加collect关系出错");
    }
}
