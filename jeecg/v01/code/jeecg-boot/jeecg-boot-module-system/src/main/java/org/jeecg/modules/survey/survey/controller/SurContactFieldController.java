package org.jeecg.modules.survey.survey.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.survey.survey.entity.SurContactField;
import org.jeecg.modules.survey.survey.entity.SurGroup;
import org.jeecg.modules.survey.survey.service.ISurContactFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.controller
 * @Author: GGB
 * @CreateTime: 2022-11-02  20:53
 * @Description: TODO
 * @Version: 1.0
 */
@Api(tags="sur_contact_field")
@RestController
@RequestMapping("/survey/surContactField")
@Slf4j
public class SurContactFieldController extends JeecgController<SurContactField, ISurContactFieldService> {
    @Autowired
    ISurContactFieldService surContactFieldService;

    @PostMapping()
    public Result<String> addContactField(@RequestBody SurContactField surContactField){
        Result<String> result = new Result<>();
        if(oConvertUtils.isEmpty(surContactField.getName())){
            result.error500("请输入自定义字段名!");
            result.setSuccess(false);
            return result;
        }
        if(surContactFieldService.addContactField(surContactField.getName())){
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
    public Result<String> deleteSurContactField(@RequestParam("id") String id){
        Result<String> result = new Result<>();
        if(oConvertUtils.isEmpty(id)){
            result.error500("id不能为空!");
            result.setSuccess(false);
            return result;
        }
        if(surContactFieldService.deleteContactField(id)){
            return result.ok("删除成功!");
        }else {
            result.error500("删除失败!");
            result.setSuccess(false);
            return result;
        }
    }

    @PutMapping()
    public Result<String> updateSurContactField(@RequestBody SurContactField surContactField){
        Result<String> result = new Result<>();
        if(oConvertUtils.isEmpty(surContactField.getName())){
            result.error500("请输入群组名!");
            result.setSuccess(false);
            return result;
        }
        if(oConvertUtils.isEmpty(surContactField.getId())){
            result.error500("群组id不能为Null!");
            result.setSuccess(false);
            return result;
        }
        if(surContactFieldService.updateContactField(surContactField.getId(),surContactField.getName())){
            return result.ok("修改成功!");
        }else {
            result.error500("修改失败!");
            result.setSuccess(false);
            return result;
        }
    }

    @GetMapping()
    public Result<List<SurContactField>> getSurGroupList(){
        Result<List<SurGroup>> result = new Result<>();
        List<SurContactField> surContactFieldList = surContactFieldService.getContactFields();
        return result.ok(surContactFieldList);
    }
}
