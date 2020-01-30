package com.ecnu.onion.vo;

import com.ecnu.onion.excpetion.CommonServiceException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author onion
 * @date 2020/1/29 -10:31 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NoteVO extends BaseRequestVO{
    private String authorEmail;
    private String authorName;
    private String title;
    private String content;
    private Integer types;
    @Override
    public void checkParams() throws CommonServiceException {

    }
}
