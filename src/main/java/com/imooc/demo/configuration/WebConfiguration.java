package com.imooc.demo.configuration;

import com.imooc.demo.interceptor.LoginRequiredInterceptor;
import com.imooc.demo.interceptor.PassPortInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author emperor
 * @Date 2019/11/21 20:00
 * @Version 1.0
 */
@Component
public class WebConfiguration  extends WebMvcConfigurerAdapter {
    @Autowired
    private PassPortInterceptor passportInterceptor;
    @Autowired
    private LoginRequiredInterceptor loginRequriedInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(passportInterceptor);
        //registry.addInterceptor(loginRequriedInterceptor);
        //registry.addInterceptor(loginRequriedInterceptor).addPathPatterns("/setting*");
        super.addInterceptors(registry);
    }

}
