package cn.swunlp.auth.entity;

import cn.swunlp.backend.base.security.entity.ApplicationPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用信息
 * @author TangXi
 * @since 2024/2/1
 */

@Data
@EqualsAndHashCode
public class ApplicationInfo {
    /**
     * 应用id
     */
    private String id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用代码
     */
    private String code;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 是否核心应用
     */
    private boolean core;

    /**
     * 是否可访问
     */
    private boolean accessible;

    /**
     * 应用是否上线
     */
    private boolean online;

    /*
     * 挂载的路由路径
     */
    private String routePath;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private String createTime;
}
