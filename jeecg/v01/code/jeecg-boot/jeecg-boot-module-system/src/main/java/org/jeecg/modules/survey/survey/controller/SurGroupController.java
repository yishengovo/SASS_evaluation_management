package org.jeecg.modules.survey.survey.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.survey.survey.entity.SurGroup;
import org.jeecg.modules.survey.survey.service.ISurGroupContactService;
import org.jeecg.modules.survey.survey.service.ISurGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.controller
 * @Author: GGB
 * @CreateTime: 2022-11-02  17:06
 * @Description: TODO
 * @Version: 1.0
 */
@Api(tags="sur_group")
@RestController
@RequestMapping("/survey/surGroup")
@Slf4j
public class SurGroupController  extends JeecgController<SurGroup, ISurGroupService> {
    @Autowired
    ISurGroupService surGroupService;
    @Autowired
    ISurGroupContactService surGroupContactService;

    /**
     * @description: 新建群组
     * @author: GGB
     * @date: 2022/11/2 17:21
     * @param: surGroup
     * @return: Result<String>
     **/
    @PostMapping()
    public Result<String> addSurGroup(@RequestBody SurGroup surGroup){
        Result<String> result = new Result<>();
        if(oConvertUtils.isEmpty(surGroup.getName())){
            result.error500("请输入群组名!");
            result.setSuccess(false);
            return result;
        }
        if(surGroupService.addGroup(surGroup.getName())){
            return result.ok("新建成功!");
        }else {
            result.error500("新建失败!");
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * @description: 删除群组
     * @author: GGB
     * @date: 2022/11/2 17:26
     * @param: id
     * @return: Result<String>
     **/
    @DeleteMapping()
    public Result<String> deleteSurGroup(@RequestParam("id") String id){
        Result<String> result = new Result<>();
        if(oConvertUtils.isEmpty(id)){
            result.error500("id不能为空!");
            result.setSuccess(false);
            return result;
        }
        if(surGroupService.deleteGroup(id)){
            return result.ok("删除成功!");
        }else {
            result.error500("删除失败!");
            result.setSuccess(false);
            return result;
        }
    }

    @PutMapping()
    public Result<String> updateSurGroup(@RequestBody SurGroup surGroup){
        Result<String> result = new Result<>();
        if(oConvertUtils.isEmpty(surGroup.getName())){
            result.error500("请输入群组名!");
            result.setSuccess(false);
            return result;
        }
        if(oConvertUtils.isEmpty(surGroup.getId())){
            result.error500("群组id不能为Null!");
            result.setSuccess(false);
            return result;
        }
        if(surGroupService.updateGroup(surGroup.getId(),surGroup.getName())){
            return result.ok("修改成功!");
        }else {
            result.error500("修改失败!");
            result.setSuccess(false);
            return result;
        }
    }

    @GetMapping()
    public Result<List<SurGroup>> getSurGroupList(){
        Result<List<SurGroup>> result = new Result<>();
        List<SurGroup> surGroupList = surGroupService.getGroups();
        surGroupList.stream().forEach(i->{
            Integer count = surGroupContactService.getCountByGroupId(i.getId());
            if(count!=null){
                i.setCount(count);
            }else {
                i.setCount(0);
            }
        });
        return result.ok(surGroupList);
    }
}
