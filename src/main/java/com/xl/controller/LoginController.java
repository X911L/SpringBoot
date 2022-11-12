package com.xl.controller;

import com.xl.common.util.JWTUtils;
import com.xl.pojo.Token;
import com.xl.pojo.User;
import com.xl.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author xl
 * @date 2021年10月26日 14:11
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(String phone, String password) {
        return userService.login(phone, password);
    }

    @PostMapping("/registry")
    public String registry(@RequestBody User user) {
        return userService.registry(user);
    }

    @GetMapping("/user")
    public User getUserInfo(Token token) {
        return userService.getUserInfo(token.getUserId());
    }
}
