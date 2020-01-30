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
    @GetMapping("/updateNote")
    BaseResponseVO updateNote(@RequestParam String oldNoteId, @RequestParam String newNoteId);
}
