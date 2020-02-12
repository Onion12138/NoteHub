package com.ecnu.onion.api;

import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author onion
 * @date 2020/1/30 -9:56 上午
 */
@FeignClient(value = "graph-recommendation")
public interface GraphAPI {
    @GetMapping("/note/deleteNote")
    BaseResponseVO deleteNote(@RequestParam String noteId);

    @GetMapping("/user/forkNote")
    BaseResponseVO addForkRelation(@RequestParam String email, @RequestParam String noteId);

    @GetMapping("/user/viewNote")
    BaseResponseVO addViewRelation(@RequestParam String email, @RequestParam String noteId);

    @GetMapping("/user/starNote")
    BaseResponseVO addStarRelation(@RequestParam String email, @RequestParam String noteId);

    @GetMapping("/user/hateNote")
    BaseResponseVO addHateRelation(@RequestParam String email, @RequestParam String noteId);
}
