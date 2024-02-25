package cn.swunlp.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 应用信息DTO
 * @author TangXi
 * @since 2024/2/1
 */

@Data
@Schema(description = "应用信息DTO")
public class ApplicationInfoApplyDTO {

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;
}
