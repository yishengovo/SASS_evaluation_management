package org.jeecg.modules.survey.survey.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.entity
 * @Author: GGB
 * @CreateTime: 2022-11-02  16:32
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName("sur_group_contact")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sur_group_contact对象", description="sur_group_contact")
public class SurGroupContact implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**群组id*/
    @Excel(name = "群组id", width = 15)
    @ApiModelProperty(value = "群组id")
    private java.lang.String groupId;
    /**联系人id*/
    @Excel(name = "联系人id", width = 15)
    @ApiModelProperty(value = "联系人id")
    private java.lang.String contactId;
    /**创建时间*/
    @Excel(name = "创建时间", width = 15)
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**租户id*/
    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
}
