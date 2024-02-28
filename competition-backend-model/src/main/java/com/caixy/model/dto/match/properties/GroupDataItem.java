package com.caixy.model.dto.match.properties;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 比赛分组配置
 *
 * @name: com.caixy.model.dto.match.properties.GroupDataItem
 * @author: CAIXYPROMISE
 * @since: 2024-02-26 00:54
 **/
@Data
public class GroupDataItem implements Serializable
{
    private Long id;
    private Integer index;
    private String parentGroupName;

    @Max(100)
    private List<List<Option>> permission;

    @Max(1000)
    private Integer maxTeamNum;

    @Size(max = 1024)
    private String decs;

    @Max(100)
    private List<GroupDataItem> children;

    private static final long serialVersionUID = 1L;

    @Data
    public static class Option implements Serializable
    {
        private String label;
        private String value;
        private String key;
        @Size(max = 100)
        private List<Option> children;
        private static final long serialVersionUID = 1L;
    }
    // 构建从id到parentGroupName的映射
    public static void buildIdToParentGroupNameMap(List<GroupDataItem> groupDataItems,
                                                   String parentGroupName,
                                                   HashMap<Long, String> idToParentGroupNameMap)
    {
        for (GroupDataItem item : groupDataItems)
        {
            // 使用当前节点的parentGroupName，如果当前节点没有parentGroupName，则使用上级节点的名称
            String currentParentGroupName =
                    item.getParentGroupName() != null ? item.getParentGroupName() : parentGroupName;
            idToParentGroupNameMap.put(item.getId(), currentParentGroupName);
            if (item.getChildren() != null && !item.getChildren().isEmpty())
            {
                buildIdToParentGroupNameMap(item.getChildren(), currentParentGroupName, idToParentGroupNameMap);
            }
        }
    }
}
