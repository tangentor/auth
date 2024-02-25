package cn.swunlp.auth.service;

import cn.swunlp.backend.base.web.constant.AuthResult;

/**
 * @author TangXi
 * @since 2024/2/1
 */
public interface AuthService {
    AuthResult authFromGateway(String token,String uri);
}
