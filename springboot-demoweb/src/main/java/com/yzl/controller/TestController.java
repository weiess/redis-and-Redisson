package com.yzl.controller;


import com.github.pagehelper.PageInfo;
import com.yzl.pojo.BlogType;
import com.yzl.service.BlogTypeService;
import com.yzl.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/a")
public class TestController {


    @RequestMapping("/a")
    public String getDate(){
        return "ok";
    }

    @Autowired
    private BlogTypeService blogTypeService;


    @Autowired
    private RedisUtil redisUtil;


    @RequestMapping("/getData")
    public Map<String,Object> getData(){
        log.info("查询所有Blog");
        Map map = new HashMap(5);
        List list = blogTypeService.getAllType();
        map.put("data",list);
        return map;
    }


    @RequestMapping("/getDataByPage")
    public Map<String,Object> getDataPage(){
        log.info("查询所有BlogBypage");
        Map map = new HashMap(5);
        PageInfo<BlogType> blogPageInfo = blogTypeService.getTypebyPage();
        map.put("total",blogPageInfo.getTotal());
        map.put("rows",blogPageInfo.getList());
        return map;
    }


    @RequestMapping("/insertData")
    public boolean insertBolg(){
        log.info("插入BlogType");
        BlogType type = new BlogType();
        type.setIsShow(1);
        type.setTypeName("a1");
        blogTypeService.insertBlogType(type);
        return true;

    }

    @GetMapping("redis")
    public Object getRedis(){
        redisUtil.set("r","aaaaaa");
        return redisUtil.get("r");
    }

}
