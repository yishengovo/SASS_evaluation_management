package org.jeecg.modules.survey.survey.controller;

import cn.hutool.json.JSONArray;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.survey.survey.entity.SurContact;
import org.jeecg.modules.survey.survey.entity.SurContactFieldInformation;
import org.jeecg.modules.survey.survey.entity.SurGroupContact;
import org.jeecg.modules.survey.survey.model.SurContactModel;
import org.jeecg.modules.survey.survey.service.ISurContactFieldInformationService;
import org.jeecg.modules.survey.survey.service.ISurContactService;
import org.jeecg.modules.survey.survey.service.ISurGroupContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.controller
 * @Author: GGB
 * @CreateTime: 2022-11-02  22:17
 * @Description: TODO
 * @Version: 1.0
 */
@Api(tags="sur_contact")
@RestController
@RequestMapping("/survey/SurContact")
@Slf4j
public class SurContactController extends JeecgController<SurContact, ISurContactService> {
    @Autowired
    ISurContactService surContactService;
    @Autowired
    ISurContactFieldInformationService surContactFieldInformationService;
    @Autowired
    ISurGroupContactService surGroupContactService;

    /**
     * @description: 添加联系人
     * @author: GGB
     * @date: 2022/11/3 21:50
     * @param: surContactModel
     * @return: Result<String>
     **/
    @PostMapping("/addSurContact")
    public Result<String> addSurContact(@RequestBody SurContactModel surContactModel){
        Result<String> result = new Result<>();
        SurContact surContact = surContactModel.getSurContact();
        List<SurContactFieldInformation> surContactFieldInformationList = surContactModel.getSurContactFieldInformationList();
        List<SurGroupContact> surGroupContactList = surContactModel.getSurGroupContactList();
        if(oConvertUtils.isEmpty(surContact.getName())){
            result.error500("联系人名字不能为空!");
            result.setSuccess(false);
            return result;
        }
        if(oConvertUtils.isEmpty(surContact.getPhone())&&oConvertUtils.isEmpty(surContact.getEmail())){
            result.error500("邮箱或手机号至少添加一个!");
            result.setSuccess(false);
            return result;
        }
        //设置群组id，Json字段
        if(surGroupContactList.size() != 0){
            JSONArray array = new JSONArray();
            surGroupContactList.stream().forEach(i->{
                array.add(i.getGroupId());
            });
            surContact.setGroupIds(array.toString());
        }

        String contactId = surContactService.addSurContact(surContact);
        if(oConvertUtils.isEmpty(contactId)){
            result.error500("添加失败!");
            result.setSuccess(false);
            return result;
        }

        if(surContactFieldInformationList.size() != 0){
            //给联系人自定义字段添加联系人id
            surContactFieldInformationList.stream().forEach(i->{
                i.setContactId(contactId);
            });
            if(!surContactFieldInformationService.saveBatch(surContactFieldInformationList)){
                result.error500("自定义字段添加失败!");
                result.setSuccess(false);
                return result;
            }
        }
        if(surGroupContactList.size() != 0){
            //给联系人群组添加联系人id
            surGroupContactList.stream().forEach(i->{
                i.setContactId(contactId);
            });
            if(!surGroupContactService.saveBatch(surGroupContactList)){
                result.error500("群组添加失败!");
                result.setSuccess(false);
                return result;
            }
        }
        return result.ok("添加成功!");
    }
    /**
     * @description: 删除联系人，同时删除自定义字段的值和该联系人关联的群组信息
     * @author: GGB
     * @date: 2022/11/3 21:49
     * @param: id
     * @return: Result<String>
     **/
    @DeleteMapping
    public Result<String> deleteContact(@RequestParam(name = "id") String id){
        if(!surContactService.removeById(id)){
            return Result.error("删除失败!");
        }
        surGroupContactService.deleteByContactId(id);
        surContactFieldInformationService.deleteByContactId(id);
        return Result.OK("删除成功!");

    }
    @PutMapping
    public Result<String> updateContact(@RequestBody SurContactModel surContactModel){
        Result<String> result = new Result<>();
        SurContact surContact = surContactModel.getSurContact();
        List<SurContactFieldInformation> surContactFieldInformationList = surContactModel.getSurContactFieldInformationList();
        List<SurGroupContact> surGroupContactList = surContactModel.getSurGroupContactList();
        if(oConvertUtils.isEmpty(surContact.getName())){
            result.error500("联系人名字不能为空!");
            result.setSuccess(false);
            return result;
        }
        if(oConvertUtils.isEmpty(surContact.getPhone())&&oConvertUtils.isEmpty(surContact.getEmail())){
            result.error500("邮箱或手机号至少添加一个!");
            result.setSuccess(false);
            return result;
        }
        //设置群组id，Json字段
        JSONArray array = new JSONArray();
        surGroupContactList.stream().forEach(i->{
            array.add(i.getGroupId());
        });
        surContact.setGroupIds(array.toString());
        surContactService.updateById(surContact);


        //修改自定义字段表
        List<String> surContactFieldInformationIdList = surContactFieldInformationService.getIdsByContactId(surContact.getId());
        if(surContactFieldInformationIdList.size()!=0 || surContactFieldInformationList.size()!=0){
            surContactFieldInformationList.stream().forEach(i->{
                if(i.getId()!=null){
                    surContactFieldInformationService.updateById(i);
                    surContactFieldInformationIdList.remove(i.getId());
                }else {
                    i.setContactId(surContact.getId());
                    surContactFieldInformationService.save(i);
                }
            });
            surContactFieldInformationIdList.forEach(i->{
                surContactFieldInformationService.removeById(i);
            });
        }

        //修改联系人关联的群组
        List<String> surGroupContactIdList = surGroupContactService.getIdsByContactId(surContact.getId());
        if(surGroupContactIdList.size()!=0 || surGroupContactList.size()!=0){
            surGroupContactList.stream().forEach(i->{
                if(i.getId()!=null){
                    surGroupContactService.updateById(i);
                    surGroupContactIdList.remove(i.getId());
                }else {
                    i.setContactId(surContact.getId());
                    surGroupContactService.save(i);
                }
            });
            surGroupContactIdList.stream().forEach(i->{
                surGroupContactService.removeById(i);
            });
        }
        return Result.OK("修改成功!");
    }

    /**
     * @description: 获取联系人
     * @author: GGB
     * @date: 2022/11/3 23:13
     * @param: groupId
     * @return: Result<?>
     **/
    @GetMapping("/getContactsByGroupId")
    public Result<?> getSurContacts(@RequestParam(name = "groupId",required = false) String groupId,@RequestParam(name = "seachName", required = false) String seachName){
        return Result.ok(surContactService.getContactList(groupId,seachName));
    }

    /**
     * @description: 获取联系人总数
     * @author: GGB
     * @date: 2022/11/3 23:13
     * @return: Result<Integer>
     **/
    @GetMapping("/getContactTotal")
    public Result<Integer> getContactTotal(){
        Integer total = surContactService.getTotal();
        if(total!=null){
            return Result.ok(total);
        }else {
            return Result.ok(0);
        }
    }

}
