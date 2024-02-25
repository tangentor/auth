package cn.swunlp.auth.service;

import cn.swunlp.backend.base.security.constant.AppRegisterResult;
import cn.swunlp.backend.base.security.entity.ApplicationPermission;

/**
 * 注册服务
 *
 * @author TangXi
 * @since 2024/1/31
 */
public interface RegisterService {
    AppRegisterResult register(ApplicationPermission applicationPermission);
}
