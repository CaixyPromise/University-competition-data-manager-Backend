package com.caixy.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件操作工具类
 *
 * @name: com.caixy.common.utils.InnerFileUtils
 * @author: CAIXYPROMISE
 * @since: 2024-02-20 02:57
 **/
public class InnerFileUtils
{
    /**
     * 判断文件是否是图片
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/21 17:00
     */
    public static boolean isImage(MultipartFile multipartFile)
    {
        if (multipartFile == null || multipartFile.isEmpty())
        {
            return false;
        }
        String contentType = multipartFile.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * 获取文件大小
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/21 17:01
     */
    public static Long getFileSize(MultipartFile multipartFile)
    {
        return multipartFile.getSize();
    }

    /**
     * 文件转File
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/2/21 17:04
     */
    public static File multipartFileToTempFile(MultipartFile multipartFile, File file, String filePath) throws IOException
    {
        // 上传文件
        file = File.createTempFile(filePath, null);
        multipartFile.transferTo(file);
        return file;
    }
}
