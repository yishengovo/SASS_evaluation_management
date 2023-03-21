package org.jeecg.modules.survey.survey.dto;

import lombok.Data;
import org.jeecg.modules.system.entity.SysUser;

import java.util.List;

@Data
//部门用户page
public class DepartUserPage {
    private Long total;
    private Long size;
    private List<SysUser> records;
}
