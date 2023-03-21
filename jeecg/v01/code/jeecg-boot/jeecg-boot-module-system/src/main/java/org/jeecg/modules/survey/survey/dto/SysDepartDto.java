package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

import java.util.List;

@Data
//部门加人的树形结构
public class SysDepartDto {
    //部门id
    private String id;
    //部门名字
    private String departName;
    //部门排序
    private Integer departOrder;
//    //部门类型 1为一级部门 2为子部门
//    private String orgType;
    //是否为叶子标志
    private Boolean isLeaf;
    //子部门数组
    private List<SysChildrenDepartDto> children;
}
