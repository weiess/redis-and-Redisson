package com.yzl.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yzl.mapper.BlogTypeMapper;
import com.yzl.pojo.BlogType;
import com.yzl.service.BlogTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@Transactional
public class BlogTypeServiceImpl implements BlogTypeService {

    @Autowired
    private BlogTypeMapper blogTypeMapper;


    @Override
    public boolean insertBlogType(BlogType type) {
        int res = blogTypeMapper.insertBlogType(type);
        log.info("插入type的id="+type.getId());
        int a = 1/0;
        if(res == 0){
            return false;
        }
        return true;
    }


    @Override
    public PageInfo<BlogType> getTypebyPage() {
        PageHelper.startPage(1,2);
        List<BlogType> list = blogTypeMapper.getAllTypes();
        return new PageInfo<BlogType>(list);
    }

    @Override
    public List<BlogType> getAllType() {
        List<BlogType> list = blogTypeMapper.getAllTypes();
        return list;
    }
}
