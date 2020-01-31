package com.ecnu.onion.api;

import com.ecnu.onion.config.FeignConfig;
import com.ecnu.onion.vo.BaseResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author onion
 * @date 2020/1/29 -10:55 上午
 */
@FeignClient(value = "search", configuration = FeignConfig.class)
public interface SearchAPI {
    @GetMapping("/delete")
    BaseResponseVO deleteNote(@RequestParam String noteId);
}
