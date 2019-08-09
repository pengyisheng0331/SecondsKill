package com.yisen.miaosha.controller;

import com.yisen.miaosha.domain.User;
import com.yisen.miaosha.redis.RedisService;
import com.yisen.miaosha.redis.UserKey;
import com.yisen.miaosha.result.CodeMsg;
import com.yisen.miaosha.result.Result;
import com.yisen.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/thymeleaf")
    public String  thymeleaf(Model model) {
        model.addAttribute("name", "yisen");
        return "hello";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello(){
        return Result.success("hello");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User  user  = redisService.get(UserKey.getById, ""+1, User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user  = new User();
        user.setId(2);
        user.setName("1111");
        redisService.set(UserKey.getById, ""+1, user);//UserKey:id1
        return Result.success(true);
    }

    @RequestMapping("/redis/incr")
    @ResponseBody
    public Result<Long> redisIncr() {
        Long value = redisService.incr(UserKey.getById, ""+2);
        return Result.success(value);
    }

    @RequestMapping("/redis/decr")
    @ResponseBody
    public Result<Long> redisDecr() {
        Long value = redisService.decr(UserKey.getById, ""+2);
        return Result.success(value);
    }

}
