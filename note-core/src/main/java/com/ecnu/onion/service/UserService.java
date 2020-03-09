package com.ecnu.onion.service;

import com.ecnu.onion.domain.CollectNote;
import com.ecnu.onion.domain.MindMap;
import com.ecnu.onion.vo.LoginVO;
import com.ecnu.onion.vo.ModificationVO;
import com.ecnu.onion.vo.RegisterVO;
import com.ecnu.onion.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -5:49 下午
 */
public interface UserService {

    void register(RegisterVO registerVO);

    Map<String, Object> login(LoginVO loginVO);

    String uploadProfile(String email, MultipartFile file);

    void modifyPassword(String email, ModificationVO modificationVO);

    void sendCode(String email);

    void collectNote(String email, CollectNote collectNote);

    void modifyUsername(String email, String username);

    UserVO findUser(String email);

    void addMindMap(String email, MindMap mindMap);

    List<MindMap> findMindMap(String email);

    void mindMapNote(String email, CollectNote collectNote);

    MindMap getCollection(String email);

}
