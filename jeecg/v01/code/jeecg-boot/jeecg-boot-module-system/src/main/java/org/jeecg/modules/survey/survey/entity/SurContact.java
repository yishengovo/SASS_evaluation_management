package org.jeecg.modules.survey.survey.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.entity
 * @Author: GGB
 * @CreateTime: 2022-11-02  16:18
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName("sur_contact")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sur_contact对象", description="sur_contact")
public class SurContact implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**名字*/
    @Excel(name = "名字", width = 15)
    @ApiModelProperty(value = "名字")
    private java.lang.String name;
    /**手机号*/
    @Excel(name = "手机号", width = 15)
    @ApiModelProperty(value = "手机号")
    private java.lang.String phone;
    /**邮箱*/
    @Excel(name = "邮箱", width = 15)
    @ApiModelProperty(value = "邮箱")
    private java.lang.String email;
    /**地址*/
    @Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private java.lang.String address;
    /**群组*/
    @Excel(name = "群组", width = 15)
    @ApiModelProperty(value = "群组")
    private java.lang.String groupIds;
    /**性别*/
    @Excel(name = "性别", width = 15)
    @ApiModelProperty(value = "性别")
    private java.lang.String sex;
    /**生日*/
    @Excel(name = "生日", width = 15)
    @ApiModelProperty(value = "生日")
    private Date birthday;
    /**公司*/
    @Excel(name = "公司", width = 15)
    @ApiModelProperty(value = "公司")
    private java.lang.String company;
    /**职位*/
    @Excel(name = "职位", width = 15)
    @ApiModelProperty(value = "职位")
    private java.lang.String position;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
    /**固定电话*/
    @Excel(name = "固定电话", width = 15)
    @ApiModelProperty(value = "固定电话")
    private java.lang.String fixedPhone;
    /**来源*/
    @Excel(name = "来源", width = 15)
    @ApiModelProperty(value = "来源")
    private java.lang.String source;
    /**最近联系*/
    @Excel(name = "最近联系", width = 15)
    @ApiModelProperty(value = "最近联系")
    private java.util.Date recentContact;
    /**创建时间*/
    @Excel(name = "创建时间", width = 15)
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**修改时间*/
    @Excel(name = "修改时间", width = 15)
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
    /**租户id*/
    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /*群组名List*/
    @TableField(exist = false)
    private List<String> groupNameList;
}
