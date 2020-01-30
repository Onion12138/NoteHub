package com.ecnu.onion.api;

import com.ecnu.onion.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author onion
 * @date 2020/1/29 -10:55 上午
 */
@FeignClient(value = "search", configuration = FeignConfig.class)
public interface AnalyzeAPI {
//    @RequestMapping(value = "/analyze?content='hello world'", method = RequestMethod.GET)
//    String analyze(@RequestParam String content);
//    @RequestMapping(value = "/test", method = RequestMethod.GET)
//    String test();

    @GetMapping("/test")
    String test();
}
