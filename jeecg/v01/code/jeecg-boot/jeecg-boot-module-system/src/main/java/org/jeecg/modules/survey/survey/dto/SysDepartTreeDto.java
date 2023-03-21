package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

@Data
//部门加人的树形结构
public class SysDepartTreeDto {
    //父部门
    private SysDepartDto depart;
}
