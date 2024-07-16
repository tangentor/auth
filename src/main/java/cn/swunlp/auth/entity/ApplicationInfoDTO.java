package cn.swunlp.auth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用信息
 * @author TangXi
 * @since 2024/2/1
 */

@Data
@EqualsAndHashCode
public class ApplicationInfoDTO {

    /**
     * 应用名称
     */
    private String name;

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
}
