package org.jeecg.modules.survey.survey.dto;

import lombok.Data;
import org.jeecg.modules.system.entity.SysUser;

import java.util.List;

@Data
//子部门
public class SysChildrenDepartDto {
    //部门id
    private String id;
    //父亲部门id
    private String parentId;
    //部门名字
    private String departName;
    //部门排序
    private Integer departOrder;
//    //部门类型 1为一级部门 2为子部门
//    private String orgType;
    //是否为叶子标志
    private Boolean isLeaf;
    private List<SysChildrenDepartDto> children;
    //用户列表
    private List<SysUser> userList;
}
