package cn.swunlp.auth.service;

import cn.swunlp.auth.entity.*;

import java.util.List;

/**
 * @author TangXi
 * @since 2024/2/1
 */
public interface ApplicationService {

    /**
     * 检查应用信息
     * @param appName   应用名称
     * @param appCode   应用编码
     */
    void checkAppInfo(String appName, String appCode, String prefix);

    /**
     * 申请应用
     */
    boolean apply(ApplicationInfoApplyDTO applicationInfoApplyDTO);

    /**
     * 获取某个用户申请的所有应用信息
     */
    List<ApplicationInfoVO> listByUser();

    /**
     * 编辑应用信息
     */
    boolean edit(ApplicationInfoDTO applicationInfo);

    /**
     * 删除应用信息
     */
    boolean delete(String id);

    ApplicationInfoVO detail(String id);

    boolean updateAuth(ApplicationAuthInfoDTO authInfo);
}
