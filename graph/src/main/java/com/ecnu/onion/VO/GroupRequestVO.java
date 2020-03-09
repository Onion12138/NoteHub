package com.ecnu.onion.VO;

import lombok.Data;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:26 下午
 */
@Data
public class GroupRequestVO {
    private String ownerEmail;
    private String groupName;
    private List<String> partnerEmails;
}
