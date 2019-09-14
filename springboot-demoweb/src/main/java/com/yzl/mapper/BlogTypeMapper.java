package com.yzl.mapper;

import com.yzl.pojo.BlogType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Mapper
public interface BlogTypeMapper {

    //查询所有博客分类
    List<BlogType> getAllTypes();
    //修改博客分类
    Integer updateBlogType(BlogType blogType);
    //插入分类
    int insertBlogType(BlogType blogType);
    //删除博客分类
    int deleteBlogType(Integer id);
    //根据id查询是否有该博客
    BlogType findById(Integer id);

}
