package com.caixy.contentservice.controller.inner;

import com.caixy.contentservice.config.CosClientConfig;
import com.caixy.model.dto.feign.FileUploadInnerRequest;
import com.caixy.serviceclient.service.FileFeignClient;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;

/**
 * 文件上传与保存的远程调用接口
 *
 * @name: com.caixy.contentservice.controller.inner.FileServiceInnerController
 * @author: CAIXYPROMISE
 * @since: 2024-02-20 16:44
 **/
@RestController
@RequestMapping("/inner/file")
@Slf4j
public class FileServiceInnerController implements FileFeignClient
{
    @Resource
    private COSClient cosClient;
    @Resource
    private CosClientConfig cosClientConfig;
    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     * @return
     */
    @Override
    @PostMapping("/upload")
    public Boolean uploadFile(@RequestBody FileUploadInnerRequest request)
    {
        try{
            log.info("上传文件，key: {}, localFilePath: {}", request.getKey(), request.getLocalFilePath());
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(cosClientConfig.getBucket(), request.getKey(), new File(request.getLocalFilePath()));
            cosClient.putObject(putObjectRequest);
//        cosManager.putObject(key, localFilePath);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
