package com.spr.sec.web.controller;


import com.spr.sec.web.domain.Admin;
import com.spr.sec.web.domain.User;
import com.spr.sec.web.mng.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class BasicAuthController {

    @Autowired
    private UserManager userManager;

    @GetMapping("/greeting")
    public String greeting(){
        return "hello";
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/user/list")
    public List<User> studentList(@AuthenticationPrincipal Admin admin, Model model){
        return userManager.myUserList(admin.getId());
    }
}
