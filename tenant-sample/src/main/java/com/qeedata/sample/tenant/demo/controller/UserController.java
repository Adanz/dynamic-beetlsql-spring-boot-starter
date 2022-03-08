package com.qeedata.sample.tenant.demo.controller;

import com.qeedata.data.tenant.context.TenantCodeHolder;
import com.qeedata.sample.tenant.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("users")
    public List getUserList(@RequestParam("tenantId") String tenantId) {
        TenantCodeHolder.setTenantCode(tenantId);
        return userService.getUserList();
    }

    @GetMapping("users2")
    public List getUserList2(@RequestParam("tenantId") String tenantId) {
        TenantCodeHolder.setTenantCode(tenantId);
        return userService.getUserList2();
    }

    @GetMapping("update")
    public void updateUser(@RequestParam("tenantId") String tenantId) {
        TenantCodeHolder.setTenantCode(tenantId);
        userService.updateUser();
    }

    @GetMapping("beans")
    public List getBeans() {
        return userService.getBeans();
    }

}


