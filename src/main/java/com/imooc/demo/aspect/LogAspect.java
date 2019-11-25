package com.imooc.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class LogAspect {

    @Before("execution(* com.imooc.demo.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for(Object arg: joinPoint.getArgs()){
            sb.append("arg:" + arg.toString() + "|");
        }
        log.info("before method: " + sb.toString());
    }
    @After("execution(* com.imooc.demo.controller.*Controller.*(..))")
    public void afterMethod(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for(Object arg: joinPoint.getArgs()){
            sb.append("arg:" + arg.toString() + "|");
        }
        log.info("after method: " + sb.toString());
    }
}
