package com.ecnu.onion.api;

import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author onion
 * @date 2020/1/30 -9:56 上午
 */
@FeignClient(value = "graph-recommendation")
public interface GraphAPI {

    @PostMapping("/user/forkNote")
    BaseResponseVO addForkRelation(@RequestParam String email, @RequestParam String noteId);

    @PostMapping("/user/viewNote")
    BaseResponseVO addViewRelation(@RequestParam String email, @RequestParam String noteId);

    @PostMapping("/user/starNote")
    BaseResponseVO addStarRelation(@RequestParam String email, @RequestParam String noteId);

    @PostMapping("/user/hateNote")
    BaseResponseVO addHateRelation(@RequestParam String email, @RequestParam String noteId);

    @PostMapping("/user/collectNote")
    BaseResponseVO addCollectRelation(@RequestParam String email, @RequestParam String noteId);
}
