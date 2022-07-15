package com.tutti.backend.controller;

import com.tutti.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class ConfirmController {
    private final UserService userService;

    //    USER service DI
    @Autowired
    public ConfirmController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/page")
    public void viewRedirect(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Location", "https://tuttimusic.shop");
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/confirm-email")
    public String viewConfirmEmail(@Valid @RequestParam String token) {
        userService.confirmEmail(token);
        return "redirect:/page";
    }
}

