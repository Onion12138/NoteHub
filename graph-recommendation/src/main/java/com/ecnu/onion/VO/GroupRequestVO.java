package com.ecnu.onion.VO;

import com.ecnu.onion.enums.ServiceEnum;
import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.vo.BaseRequestVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/23 -5:26 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupRequestVO extends BaseRequestVO {
    private String ownerEmail;
    private String groupName;
    private List<String> partnerEmails;
    @Override
    public void checkParams() throws CommonServiceException {
        if (partnerEmails == null || partnerEmails.size() == 0) {
            throw new CommonServiceException(ServiceEnum.GROUP_CREATE_ERROR);
        }
    }
}
