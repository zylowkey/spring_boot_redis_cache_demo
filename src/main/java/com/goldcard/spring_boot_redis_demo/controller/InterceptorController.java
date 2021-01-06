package com.goldcard.spring_boot_redis_demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/interceptor")
public class InterceptorController {
    @RequestMapping("/start")
    public String start(){
        System.out.println("执行处理器逻辑");
        return "/welcome";
    }
}
