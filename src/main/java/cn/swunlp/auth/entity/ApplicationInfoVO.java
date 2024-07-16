package cn.swunlp.auth.entity;

import cn.swunlp.backend.base.security.entity.ApplicationPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 应用信息
 * @author TangXi
 * @since 2024/2/1
 */

@Data
@Schema(description = "应用信息VO")
public class ApplicationInfoVO {
    /**
     * 应用id
     */
    @Schema(description = "应用id")
    private String id;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用代码
     */
    @Schema(description = "应用代码")
    private String code;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 是否可访问
     */
    @Schema(description = "是否可访问")
    private boolean accessible;

    /**
     * 应用是否上线
     */
    @Schema(description = "应用是否上线")
    private boolean online;

    /**
     * 是否核心应用
     */
    @Schema(description = "是否核心应用")
    private boolean core;



    /*
     * 挂载的路由路径
     */
    @Schema(description = "挂载的路由路径")
    private String routePath;

    /**
     * 应用权限
     */
    @Schema(description = "路由权限列表")
    private List<RoutePermission> routePermissions;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;
}
