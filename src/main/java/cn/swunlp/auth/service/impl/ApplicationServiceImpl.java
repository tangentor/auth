package cn.swunlp.auth.service.impl;

import cn.swunlp.auth.cache.AppCheckCache;
import cn.swunlp.auth.cache.AppInfoCache;
import cn.swunlp.auth.entity.ApplicationInfo;
import cn.swunlp.auth.entity.ApplicationInfoApplyDTO;
import cn.swunlp.auth.entity.ApplicationInfoVO;
import cn.swunlp.auth.entity.RoutePermission;
import cn.swunlp.auth.exception.AppApplyException;
import cn.swunlp.auth.exception.AppRegisterException;
import cn.swunlp.auth.manager.RoutePermissionManager;
import cn.swunlp.auth.service.ApplicationService;
import cn.swunlp.backend.base.security.constant.AppRegisterResult;
import cn.swunlp.backend.base.web.util.BeanUtilsExt;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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

    private final RoutePermissionManager routePermissionManager;

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
        String uid = appCheckCache.get(appName);
        // 根据uid快速查找应用信息
        Optional<ApplicationInfo> optional = appInfoCache.lGetAll(uid).stream().filter(appInfo -> appInfo.getCode().equals(appCode)).findFirst();
        if(optional.isEmpty()){
            throw new AppRegisterException(AppRegisterResult.APP_CODE_NOT_MATCH.getName());
        }
        // 找到的信息
        ApplicationInfo applicationInfo = optional.get();
        // 挂载路由前缀
        applicationInfo.setRoutePath(prefix);
        // 更新上线信息
        setStatus(uid,applicationInfo,true);
    }

    private void setStatus(String uid,ApplicationInfo applicationInfo,boolean online){
        List<ApplicationInfo> applicationInfoList = appInfoCache.lGetAll(uid);
        int index = 0;
        for(ApplicationInfo info : applicationInfoList){
            if(info.getId().equals(applicationInfo.getId())){
                break;
            }
            index++;
        }
        applicationInfo.setOnline(online);
        if(!online) {
            applicationInfo.setRoutePath(null);
        }
        appInfoCache.lUpdateIndex(uid,index,applicationInfo);
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
        uid = "admin";
        applyInfo.setCode(code);
        applyInfo.setId(UUID.randomUUID().toString().replace("-", ""));
        applyInfo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        applyInfo.setCreator(uid);
        // 设置默认可访问
        applyInfo.setAccessible(true);
        appCheckCache.set(applicationInfo.getName(), uid);
        appInfoCache.lSet(uid, applyInfo);
        return true;
    }

    private String generateCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    @Override
    public List<ApplicationInfoVO> listByUser() {
        String uid = "admin";
        return appInfoCache.lGetAll(uid).stream().map(
                appInfo -> {
                    ApplicationInfoVO infoVO = BeanUtilsExt.copyBean(appInfo, new ApplicationInfoVO());
                    if(infoVO.isOnline() && StringUtils.hasText(appInfo.getRoutePath())){
                        List<RoutePermission> appRoutePermission = routePermissionManager.getAppRoutePermission(appInfo.getRoutePath());
                        if(appRoutePermission.isEmpty()){
                            setStatus(uid,appInfo,false);
                        } else {
                            infoVO.setRoutePermissions(appRoutePermission);
                        }
                    }
                    return infoVO;
                }
        ).toList();
    }

    @Override
    public boolean edit(ApplicationInfo applicationInfo) {
        return false;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }
}
