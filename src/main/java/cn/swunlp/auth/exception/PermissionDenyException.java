package cn.swunlp.auth.exception;

import cn.swunlp.backend.base.base.exception.BusinessException;

/**
 * 无权限操作异常
 * @author TangXi
 * @since 2024/2/1
 */

public class PermissionDenyException extends BusinessException {
    public PermissionDenyException(String msg) {
        super(msg);
    }
    // Your class implementation goes here
}
