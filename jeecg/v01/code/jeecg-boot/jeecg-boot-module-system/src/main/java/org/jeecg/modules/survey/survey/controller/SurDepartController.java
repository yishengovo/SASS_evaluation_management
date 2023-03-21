package org.jeecg.modules.survey.survey.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.model.SysDepartTreeModel;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "sys_depart")
@RestController
@RequestMapping("/survey/depart")
@Slf4j
public class SurDepartController {
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private ISysUserService sysUserService;

    //查询部门树
    @GetMapping("/queryTreeList")
    public Result<List<SysDepartTreeModel>> queryTreeList() {
//        Result<List<SysDepartTreeDto>> result = new Result<>();
//        try {
//            List<SysDepartTreeModel> list = sysDepartService.queryTreeList();
//            List<SysDepartTreeDto> departDtoList = new ArrayList<>();
//            //将list复制到departDtoList里面
//            list.forEach(sysDepartTreeModel -> {
//                SysDepartDto sysDepartDto = new SysDepartDto();
//                BeanUtils.copyProperties(sysDepartTreeModel,sysDepartDto);
//                SysDepartTreeDto sysDepartTreeDto = new SysDepartTreeDto();
//                sysDepartTreeDto.setDepart(sysDepartDto);
//                //获取children list
//                List<SysDepartTreeModel> children = sysDepartTreeModel.getChildren();
//                //如果children为空 则只是一级部门 不需要查询子部门
//                if (children == null){
//                    departDtoList.add(sysDepartTreeDto);
//                }else {
//                    List<SysChildrenDepartDto> childrenDepartDtoList = new ArrayList<>();
//                    //遍历children
//                    children.forEach(childrenDep->{
//                        SysChildrenDepartDto sysChildrenDepartDto = new SysChildrenDepartDto();
//                        BeanUtils.copyProperties(childrenDep,sysChildrenDepartDto);
//                        childrenDepartDtoList.add(sysChildrenDepartDto);
//                    });
//                    //遍历 childrenDepartDtoList 查询出子部门的员工
//                    childrenDepartDtoList.forEach(sysChildrenDepartDto -> {
//                    });
//                    sysDepartDto.setChildren(childrenDepartDtoList);
//                    departDtoList.add(sysDepartTreeDto);
//                }
//            });
//            result.setResult(departDtoList);
//            result.setSuccess(true);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return result;
//    }


        Result<List<SysDepartTreeModel>> result = new Result<>();
        try {
            // 从内存中读取
//			List<SysDepartTreeModel> list =FindsDepartsChildrenUtil.getSysDepartTreeList();
//			if (CollectionUtils.isEmpty(list)) {
//				list = sysDepartService.queryTreeList();
//			}

            List<SysDepartTreeModel> list = sysDepartService.queryTreeList();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
}
