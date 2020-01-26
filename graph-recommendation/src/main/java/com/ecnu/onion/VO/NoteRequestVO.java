package com.ecnu.onion.VO;

import com.ecnu.onion.excpetion.CommonServiceException;
import com.ecnu.onion.vo.BaseRequestVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author onion
 * @date 2020/1/25 -2:54 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NoteRequestVO extends BaseRequestVO {
    private String noteId;
    private String title;
    @Override
    public void checkParams() throws CommonServiceException {
    }
}
