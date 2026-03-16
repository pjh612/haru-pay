package com.haru.testclient.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoPageController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
