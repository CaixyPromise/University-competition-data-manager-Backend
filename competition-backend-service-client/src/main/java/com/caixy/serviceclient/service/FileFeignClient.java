package com.caixy.serviceclient.service;

import com.caixy.model.dto.feign.FileUploadInnerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Name: com.caixy.serviceclient.service.FileFeignClient
 * @Description: 文件远程内部调用端接口
 * @Author: CAIXYPROMISE
 * @Date: 2024-02-20 16:45
 **/
@FeignClient(name = "competition-backend-content-service", path = "/api/content/inner/file")
public interface FileFeignClient
{
    @PostMapping("/upload")
    Boolean uploadFile(@RequestBody FileUploadInnerRequest request);
}
