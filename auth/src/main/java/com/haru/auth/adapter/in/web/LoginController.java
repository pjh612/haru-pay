package com.haru.auth.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/member/login")
    String memberLogin() {
        return "login";
    }
}
