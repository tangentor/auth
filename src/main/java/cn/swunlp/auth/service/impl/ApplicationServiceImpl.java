package cn.swunlp.auth.service.impl;

import cn.swunlp.auth.cache.AppAuthConfigCache;
import cn.swunlp.auth.cache.AppCheckCache;
import cn.swunlp.auth.cache.AppInfoCache;
import cn.swunlp.auth.entity.*;
import cn.swunlp.auth.exception.AppApplyException;
import cn.swunlp.auth.exception.AppRegisterException;
import cn.swunlp.auth.exception.PermissionDenyException;
import cn.swunlp.auth.manager.RoutePermissionManager;
import cn.swunlp.auth.service.ApplicationService;
import cn.swunlp.backend.base.base.exception.BusinessException;
import cn.swunlp.backend.base.base.util.UserInfoUtils;
import cn.swunlp.backend.base.security.constant.AppRegisterResult;
import cn.swunlp.backend.base.web.util.BeanUtilsExt;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 应用信息服务
 * @author TangXi
 * @since 2024/2/1
 */

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    /**
     * <p>存储格式：</p>
     *      <p><br/>AppInfoCache: uid -> ApplicationInfo</p>
     *      <p><br/>AppCheckCache: appName -> uid</p>
     */
    private final AppInfoCache appInfoCache;

    private final AppCheckCache appCheckCache;
    /**
     * 是否由平台介入管理应用权
     */
    private final AppAuthConfigCache appAuthConfigCache;

    private final RoutePermissionManager routePermissionManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 检查应用信息
     * @param appName 应用名称
     * @param appCode 应用编码
     */
    @Override
    public void checkAppInfo(@NotNull String appName, String appCode, String prefix) {
        if(!appCheckCache.exist(appName)){
            throw new AppRegisterException(AppRegisterResult.APP_CODE_NOT_EXIST.getName());
        }
        // 根据uid快速查找应用信息
        ApplicationInfo info = appInfoCache.get(appName);
        String code = info.getCode();
        logger.info("应用名称：{}，应用编码：{}",appName,code);
        if(!Objects.equals(code, appCode)){
            throw new AppRegisterException(AppRegisterResult.APP_CODE_NOT_MATCH.getName());
        }
        // 挂载路由前缀
        info.setRoutePath(prefix);
        // 更新上线信息
        setStatus(appName,info,true);
    }

    private void setStatus(String name,ApplicationInfo applicationInfo,boolean online){
        applicationInfo.setOnline(online);
        if(!online) {
            applicationInfo.setRoutePath(null);
        }
        appInfoCache.set(name,applicationInfo);
    }


    @Override
    public boolean apply(ApplicationInfoApplyDTO applicationInfo) {
        String uid = appCheckCache.get(applicationInfo.getName());
        if(uid != null){
            throw new AppApplyException("应用名称已存在");
        }
        ApplicationInfo applyInfo = BeanUtilsExt.copyBeanSuper(applicationInfo, ApplicationInfo.class);
        // 生成code
        String code = generateCode();
        // 暂时默认创建者为admin
        uid = "admin";
        applyInfo.setCode(code);
        applyInfo.setId(UUID.randomUUID().toString().replace("-", ""));
        applyInfo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        applyInfo.setCreator(uid);
        // 设置默认可访问
        applyInfo.setAccessible(true);
        appCheckCache.set(applicationInfo.getName(), uid);
        // 使用appName作为key存储应用信息
        appInfoCache.set(applicationInfo.getName(), applyInfo);
        return true;
    }

    private String generateCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    @Override
    public List<ApplicationInfoVO> listByUser() {
        // 暂时不区分用户，所有都只有管理员进行操作
        List<ApplicationInfoVO> result = new ArrayList<>();
        List<String> keyList = appInfoCache.keys();
        for (String key : keyList) {
            // 只取最后一个key
            key = key.split(":")[3];
            ApplicationInfo info = appInfoCache.get(key);
            result.add(getAppInfoVO(info,true));
        }
        return result;
    }

    private ApplicationInfoVO getAppInfoVO(ApplicationInfo appInfo,boolean isBrief) {
        ApplicationInfoVO infoVO = BeanUtilsExt.copyBean(appInfo, new ApplicationInfoVO());
        if(infoVO.isOnline() && StringUtils.hasText(appInfo.getRoutePath())) {
            List<RoutePermission> appRoutePermission = routePermissionManager.getAppRoutePermission(appInfo.getRoutePath());
            if (appRoutePermission.isEmpty()) {
                setStatus(appInfo.getName(), appInfo, false);
                infoVO.setOnline(false);
            } else {
                // 是否需要返回路由权限
                if(!isBrief){
                    infoVO.setRoutePermissions(appRoutePermission);
                }
            }
        }
        return infoVO;
    }


    public void preCheck(String name) {
        // 是否是创建者
        String uid = UserInfoUtils.getUserName();
        ApplicationInfo info = appInfoCache.get(name);
        if(!info.getCreator().equals(uid)){
            throw new PermissionDenyException("无权限操作");
        }
    }
    @Override
    public boolean edit(ApplicationInfoDTO applicationInfo) {
        // 交由权限管理器进行统一校验
//        preCheck(applicationInfo.getName());
        // 先找到对应的应用信息
        ApplicationInfo oldInfo = appInfoCache.get(applicationInfo.getName());
        // 再更新应用信息
        BeanUtilsExt.copyProperties(applicationInfo, oldInfo);
        return appInfoCache.set(applicationInfo.getName(), oldInfo);
    }

    @Override
    public boolean delete(String id) {
        ApplicationInfo applicationInfo = getById(id);
//        preCheck(applicationInfo.getName());
        return appInfoCache.remove(applicationInfo.getName());
    }

    private ApplicationInfo getById(String id) {
        List<String> keyList = appInfoCache.keys();
        for (String key : keyList) {
            if(appInfoCache.get(key).getId().equals(id)){
                return appInfoCache.get(key);
            }
        }
        throw new BusinessException("应用不存在");
    }

    @Override
    public ApplicationInfoVO detail(String id) {
        ApplicationInfo info = appInfoCache.get(id);
        if(info == null) {
            throw new BusinessException("应用不存在");
        }
        return getAppInfoVO(info,false);
    }

    @Override
    public boolean updateAuth(ApplicationAuthInfoDTO authInfo) {
        // 更新对应的应用信息
        ApplicationInfo applicationInfo = appInfoCache.get(authInfo.getName());
        if(applicationInfo.isAccessible() != authInfo.isAccessible()) {
            applicationInfo.setAccessible(authInfo.isAccessible());
            appInfoCache.set(authInfo.getName(), applicationInfo);
        }
        // 平台介入更新权限
        boolean set = appAuthConfigCache.set(authInfo.getName(), authInfo.isPlatformIntervention());
        if(!set) {
            logger.error("平台介入更新权限失败");
            return false;
        }
        if(authInfo.isPlatformIntervention()) {
            // 停止心跳更新权限
            routePermissionManager.addRoutePermission(authInfo.getPrefix(),authInfo.getRoutePermissions(),-1);
        }
        return true;
    }
}
