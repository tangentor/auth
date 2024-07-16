package cn.swunlp.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 应用权限信息
 * @author TangXi
 * @since 2024/7/3
 */

@Data
@Schema(description = "应用权限信息")
public class ApplicationAuthInfoDTO {

    /**
     * 应用id
     */
    @Schema(description = "应用id")
    private String id;

    @Schema(description = "应用名称")
    private String name;

    @Schema(description = "应用前缀")
    private String prefix;

    /**
     * 是否可访问
     */
    @Schema(description = "是否可访问")
    private boolean accessible;

    @Schema(description = "是否进行平台介入")
    private boolean platformIntervention;


    /**
     * 应用权限
     */
    @Schema(description = "路由权限列表")
    private List<RoutePermission> routePermissions;
}
