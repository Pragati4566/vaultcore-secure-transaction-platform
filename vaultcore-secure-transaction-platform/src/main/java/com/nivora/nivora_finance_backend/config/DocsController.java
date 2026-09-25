package com.nivora.nivora_finance_backend.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocsController {

    @GetMapping("/docs")
    public String docs() {
        return "forward:/docs.html";
    }
}