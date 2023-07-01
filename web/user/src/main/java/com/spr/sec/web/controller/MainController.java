package com.spr.sec.web.controller;

import com.spr.sec.comp.domain.UserEntity;
import com.spr.sec.web.domain.Admin;
import com.spr.sec.web.mng.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
public class MainController {

    @ResponseBody
    @GetMapping("/auth")
    public Authentication auth(){
        /**
         * SecurityContextHolder 에서 해당 Authentication 정보 가져오기
         */
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/")
    public String main(Model model, HttpSession session){
        model.addAttribute("sessionId",session.getId());
        return "index";
    }

    @GetMapping("/login")
    public String login(){ return "loginForm";}


    @GetMapping("/login-error")
    public String loginError(Model model){
        model.addAttribute("loginError",true);
        return "loginForm";
    }

    @GetMapping("/access-denied")
    public String accessDenied(){
        return "accessDenied";
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/user-page")
    public String userPage(){
        return "userPage";
    }


    @Autowired
    UserManager userManager;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/admin-page")
    /**
     * inMemory test method
        public String adminPage(@AuthenticationPrincipal Admin admin, Model model){
            model.addAttribute("userList",userManager.myUserList(admin.getId()));
            return "adminPage";
        }
     */
    public String adminPage(@AuthenticationPrincipal UserEntity userEntity, Model model){
        model.addAttribute("userList",userManager.myUserList(userEntity.getUserId()));
        return "adminPage";
    }
}
