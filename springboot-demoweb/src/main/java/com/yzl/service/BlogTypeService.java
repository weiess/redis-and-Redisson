package com.yzl.service;

import com.github.pagehelper.PageInfo;
import com.yzl.pojo.BlogType;

import java.util.List;

public interface BlogTypeService {


    boolean insertBlogType(BlogType type);

    PageInfo<BlogType> getTypebyPage();

    List<BlogType> getAllType();

}
