package com.qeedata.sample.dynamic.beetlsql.demo.controller;

import com.qeedata.sample.dynamic.beetlsql.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("users")
    public List getUserList() {
        return userService.getUserList();
    }

    @GetMapping("users2")
    public List getUserList2() {
        return userService.getUserList2();
    }

    @GetMapping("update")
    public void updateUser() {
        userService.updateUser();
    }

    @GetMapping("beans")
    public List getBeans() {
        return userService.getBeans();
    }

}


