package com.baturu.zd.controller;

import com.baturu.parts.dtos.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;


@Controller
@RequestMapping("file")
@Slf4j
public class UploadFileController {

    /**
     * 文件的域名
     */
    @Value("${zd.picture.serverUrl}")
    private String fileDomainName;
    /**
     * 文件磁盘存储路径
     */
    @Value("${zd.picture.serverBaseUrl}")
    private String fileDiskStoragePath;



    private String logisticsPath = "btr-zd" + File.separator;



    @ResponseBody
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public ResultDTO<String> uploadFile(MultipartFile file){
        log.info("上传图片开始-------------------------------"+file.getOriginalFilename());
        if (StringUtils.isBlank(fileDiskStoragePath) || StringUtils.isBlank(fileDomainName)){
            return ResultDTO.failed("获取文件上传域名和路径失败！");
        }
        try{
            String fileSavePath = fileDiskStoragePath + logisticsPath;
            File dirFile = new File(fileSavePath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            String fileName = createFileName(file);
            String filePath = fileSavePath + File.separator + fileName;
            log.info("filePath--------------------"+filePath);
            File fileSave = new File(filePath);
            file.transferTo(fileSave);
            return ResultDTO.succeedWith(fileDomainName + logisticsPath + fileName);
        } catch (Exception e){
            log.error("UploadFileController#uploadFile Exception. ", e);
            return ResultDTO.failed("上传文件出现异常!");
        }
    }

    /**
     * 获取文件名称使用UUID生成
     * @param file 文件
     * @return 文件名称
     */
    private String createFileName(MultipartFile file) {
        synchronized(this){
            log.info("图片名称------------------："+file.getOriginalFilename());
            String fileName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            return UUID.randomUUID().toString().toLowerCase().replaceAll("-", "") + "." + fileName;
        }
    }

}
