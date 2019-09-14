package com.yzl.pojo;

import lombok.Data;

@Data
public class BlogType {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 分类名称
     */
    private String typeName;
    /**
     * 分类排序
     */
    private String orderNum;

    /**
     * 是否显示该分类，及以下所有文章
     */
    private Integer isShow;

}
