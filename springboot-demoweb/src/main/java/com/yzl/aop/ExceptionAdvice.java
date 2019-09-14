package com.yzl.aop;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Map defaultErrorHandler(HttpServletRequest request,Exception e)throws Exception{
        Map map =new HashMap(5);
        e.printStackTrace();
        map.put("data",500);
        map.put("issuccess","false");
        return map;
    }
}
