package com.yisen.miaosha.controller;

import com.yisen.miaosha.redis.RedisService;
import com.yisen.miaosha.result.Result;
import com.yisen.miaosha.service.MiaoshaUserService;
import com.yisen.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

	private static Logger log = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
    MiaoshaUserService userService;

	@Autowired
    RedisService redisService;

	/**
	 * @description: 跳转到登录页
	 * @param
	 * @return: java.lang.String
	 * @author: yisen
	 * @time: 2019/8/9 14:54
	 */
    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    /**
     * @description: 登录处理
     * @param response
     * @param loginVo
     * @return: com.yisen.miaosha.result.Result<java.lang.Boolean>
     * @author: yisen
     * @time: 2019/8/9 14:54
     */
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
    	log.info(loginVo.toString());
    	//登录
    	String token = userService.login(response, loginVo);
    	return Result.success(token);
    }
}
