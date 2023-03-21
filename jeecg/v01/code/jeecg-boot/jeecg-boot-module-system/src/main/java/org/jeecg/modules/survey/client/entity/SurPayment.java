package org.jeecg.modules.survey.client.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: sur_payment
 * @Author: jeecg-boot
 * @Date:   2022-12-09
 * @Version: V1.0
 */
@Data
@TableName("sur_payment")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sur_payment对象", description="sur_payment")
public class SurPayment implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键，商品订单号*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键，商品订单号")
    private java.lang.String id;
    /**产品描述*/
    @Excel(name = "产品描述", width = 15)
    @ApiModelProperty(value = "产品描述")
    private java.lang.String productDescription;
    /**金额*/
    @Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
    private java.lang.Integer amount;
    /**是否支付成功*/
    @Excel(name = "是否支付成功", width = 15)
    @ApiModelProperty(value = "是否支付成功")
    private java.lang.Boolean succeeded;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @Dict(dicCode = "id",dictTable = "sys_user",dicText = "username")
    @ApiModelProperty(value = "用户id")
    private java.lang.String userId;
    /**订单号*/
    @Excel(name = "订单号", width = 15)
    @ApiModelProperty(value = "订单号")
    private java.lang.String outTradeNo;
    /**充上的积分*/
    @Excel(name = "充上的积分", width = 15)
    @ApiModelProperty(value = "充上的积分")
    private java.lang.Integer integral;
    /**租户id*/
    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
}

