package com.haru.payments.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorPageController {

    @GetMapping("/error-page")
    public String errorPage(@RequestParam(required = false, defaultValue = "unknown") String code,
                            @RequestParam(required = false) String type,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) Integer status,
                            @RequestParam(required = false) String detail,
                            @RequestParam(required = false) String message,
                            Model model) {
        String displayMessage = detail != null ? detail : (message != null ? message : "오류가 발생했습니다.");
        model.addAttribute("code", code);
        model.addAttribute("type", type);
        model.addAttribute("title", title);
        model.addAttribute("status", status);
        model.addAttribute("detail", detail);
        model.addAttribute("message", displayMessage);
        return "error-page";
    }
}
