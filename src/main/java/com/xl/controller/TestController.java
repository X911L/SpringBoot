package com.xl.controller;

import com.xl.common.util.RedisUtils;
import com.xl.pojo.Token;
import com.xl.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author X
 * @date 2022年10月28日 15:28
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final TestService testService;

    @GetMapping("/12")
    public void test(Token token) {
        System.out.println("testService = " + testService);
    }

}
