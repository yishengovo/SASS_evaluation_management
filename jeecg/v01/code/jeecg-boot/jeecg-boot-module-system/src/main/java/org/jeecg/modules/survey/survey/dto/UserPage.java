package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserPage {
    private Long total;
    private Long size;
    //被评价者数组
    private List<UserDto> user;
}
