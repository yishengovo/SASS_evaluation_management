package org.jeecg.modules.survey.oss.controller;

import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.survey.oss.utils.OssUploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {
    @Autowired
    private RedisUtil redisUtil;

    @AutoLog(value = "文件上传")
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @PostMapping(value = "/upload")
    public Result<?> upload(MultipartFile file) throws IOException {
        //获取请求头
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        String tenantId = request.getHeader("tenant-id");
        String url = OssUploadFile.ossUpload(file);
        //存入redis
        redisUtil.set("oss:excel", url);
        return Result.OK(url);
    }

    @AutoLog(value = "下载模板excel")
    @ApiOperation(value = "下载模板excel", notes = "下载模板excel")
    @GetMapping(value = "/downloadExcel")
    public Result<?> download() {
        String url = (String) redisUtil.get("oss:excel");
        return Result.OK(url);
    }
}
