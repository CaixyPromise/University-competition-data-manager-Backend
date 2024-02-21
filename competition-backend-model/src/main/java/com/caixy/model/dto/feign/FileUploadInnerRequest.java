package com.caixy.model.dto.feign;

import lombok.Data;

import java.io.Serializable;

/**
 * 内部远程调用文件上传请求体
 *
 * @name: com.caixy.model.dto.feign.FileUploadInnerRequest
 * @author: CAIXYPROMISE
 * @since: 2024-02-21 01:50
 **/
@Data
public class FileUploadInnerRequest implements Serializable
{
    String key;
    String localFilePath;
    private static final long serialVersionUID = 1L;
}
