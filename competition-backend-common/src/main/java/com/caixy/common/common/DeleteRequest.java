package com.caixy.common.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 删除请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRequest implements Serializable
{

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}