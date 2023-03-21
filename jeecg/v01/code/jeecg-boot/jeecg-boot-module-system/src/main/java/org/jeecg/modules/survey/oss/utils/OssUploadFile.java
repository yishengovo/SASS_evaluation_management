package org.jeecg.modules.survey.oss.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.jeecg.modules.survey.oss.config.ConstantPropertiesUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

//文件上传工具类
public class OssUploadFile {
    public static String ossUpload(MultipartFile file) {
        try {
            String endpoint = ConstantPropertiesUtils.END_POINT;
            String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
            String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
            String bucketName = ConstantPropertiesUtils.BUCKET_NAME;
            String url = null;
            //创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            //获取上传文件输入流
            InputStream inputStream = null;
            inputStream = file.getInputStream();
            //获取文件名称
            String fileName = file.getOriginalFilename();
            //保证文件名唯一，去掉uuid中的'-'
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid + "-" + fileName;
            //把文件按日期分类，构建日期路径：avatar/2019/02/26/文件名
            // 构建日期目录
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String datePath = dateFormat.format(new Date());
            //拼接
//            String fileUrl = datePath + "/" + fileName;
            fileName = datePath + "/" + fileName;
            //调用oss方法上传到阿里云
            //第一个参数：Bucket名称
            //第二个参数：上传到oss文件路径和文件名称
            //第三个参数：上传文件输入流
            ossClient.putObject(bucketName, fileName, inputStream);
            //把上传后把文件url返回
            //https://xppll.oss-cn-beijing.aliyuncs.com/01.jpg
            url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            //关闭OSSClient
            ossClient.shutdown();
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
