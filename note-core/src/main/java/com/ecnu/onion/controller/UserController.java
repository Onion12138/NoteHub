package com.ecnu.onion.controller;

import com.ecnu.onion.domain.CollectNote;
import com.ecnu.onion.domain.MindMap;
import com.ecnu.onion.service.UserService;
import com.ecnu.onion.utils.AuthUtil;
import com.ecnu.onion.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -9:38 上午
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BaseResponseVO register(@RequestBody RegisterVO registerVO) {
        userService.register(registerVO);
        return BaseResponseVO.success();
    }

    @PostMapping("/login")
    public BaseResponseVO login(@RequestBody LoginVO loginVO){
        Map<String, Object> map = userService.login(loginVO);
        return BaseResponseVO.success(map);
    }

    @PostMapping("/uploadProfile")
    public BaseResponseVO uploadProfile(@RequestParam MultipartFile file) {
        String email = AuthUtil.getEmail();
        String url = userService.uploadProfile(email, file);
        return BaseResponseVO.success(url);
    }

    @GetMapping("/sendCode")
    public BaseResponseVO sendCode(@RequestParam String email) {
        userService.sendCode(email);
        return BaseResponseVO.success();
    }

    @PostMapping("/modifyPassword")
    public BaseResponseVO modifyPassword(@RequestBody ModificationVO modificationVO) {
        String email = AuthUtil.getEmail();
        userService.modifyPassword(email, modificationVO);
        return BaseResponseVO.success();
    }

    @PostMapping("/modifyUsername")
    public BaseResponseVO modifyUsername(@RequestParam String username) {
        String email = AuthUtil.getEmail();
        userService.modifyUsername(email, username);
        return BaseResponseVO.success();
    }

    @GetMapping("/getCollection")
    public BaseResponseVO collection() {
        String email = AuthUtil.getEmail();
        MindMap mindMap = userService.getCollection(email);
        return BaseResponseVO.success(mindMap);
    }

    @PostMapping("/collectNote")
    public BaseResponseVO collectNote(@RequestBody CollectNote collectNote){
        String email = AuthUtil.getEmail();
        userService.collectNote(email, collectNote);
        return BaseResponseVO.success();
    }

    @PostMapping("/mindMapNote")
    public BaseResponseVO mindMapNote(@RequestBody CollectNote collectNote) {
        String email = AuthUtil.getEmail();
        userService.mindMapNote(email, collectNote);
        return BaseResponseVO.success();
    }

    @GetMapping("/getMindMap")
    public BaseResponseVO getMindMap() {
        String email = AuthUtil.getEmail();
        List<MindMap> mindMapList = userService.findMindMap(email);
        return BaseResponseVO.success(mindMapList);
    }

    @PostMapping("/addMindMap")
    public BaseResponseVO addMindMap(@RequestBody MindMap mindMap) {
        String email = AuthUtil.getEmail();
        userService.addMindMap(email, mindMap);
        return BaseResponseVO.success();
    }


    @GetMapping("/findUser")
    public BaseResponseVO findUser(@RequestParam String email) {
        UserVO userVO = userService.findUser(email);
        return BaseResponseVO.success(userVO);
    }



//    @GetMapping("/findMindMap")
//    public BaseResponseVO findMindMap() {
//        String email = AuthUtil.getEmail();
//        Collection collection = userService.findMindMap(email);
//        return BaseResponseVO.success(collection);
//    }
//    @GetMapping("/insertDefaultMap")
//    public BaseResponseVO map(@RequestParam String email) {
//        userService.mindMap(email);
//        return BaseResponseVO.success();
//    }

//    @GetMapping("/insertTag")
//    public BaseResponseVO insertTag() {
//        String[] tagStr = {"人工智能","前端开发","后端开发","移动开发","测试运维","大数据","数学","学科基础","编程语言","其他"};
//        String[] level = {"机器学习 深度学习 计算机视觉 自然语言处理",
//                "前端框架 Html/css 前端工具",
//                "Spring全家桶 微服务 Python框架 关系数据库 NoSQL 分布式 后端工具",
//                "iOS开发 安卓开发 微信小程序",
//                "Linux 软件测试 虚拟化技术 服务器",
//                "云计算 数据挖掘 大数据框架 数学建模",
//                "高等数学 线性代数 离散数学 概率论与数理统计 凸优化",
//                "操作系统 计算机网络 计算机组成 数据库原理 编译原理 数据结构 算法 面向对象分析与设计",
//                "Java Python C C++ Javascript 其他语言",
//                "分类太渣 想自定义"
//        };
//        for (int i = 0; i < tagStr.length; i++) {
//            Tag tag = new Tag(tagStr[i], i, true);
//            String[] sub = level[i].split(" ");
//            for (int j = 0; j < sub.length; j++) {
//                tag.addChildren(new Tag(sub[j], (i+1)*12+j, false));
//            }
//            tagDao.save(tag);
//        }
//        return BaseResponseVO.success();
//    }

}

