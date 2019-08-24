package com.yisen.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.yisen.miaosha.domain.MiaoshaUser;
import com.yisen.miaosha.redis.AccessKey;
import com.yisen.miaosha.redis.RedisService;
import com.yisen.miaosha.result.CodeMsg;
import com.yisen.miaosha.result.Result;
import com.yisen.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @description:
 * @author: yisheng
 * @time: 2019/8/22 16:01
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            //先取用户
            MiaoshaUser user = getUser(request, response);
            //保存用户到ThreadLocal
            UserContext.setUser(user);
            //获得方法上的注解
            HandlerMethod hm = (HandlerMethod)handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null) {
                return true; //没有定义注解，放行
            }
            //获得注解上的参数
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            //key
            String key = request.getRequestURI();
            if(needLogin){ //需要登录
                if(user == null){
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();//加上用户id
            }
            //处理逻辑
            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if(count  == null) {
                redisService.set(ak, key, 1);
            }else if(count < maxCount) {
                redisService.incr(ak, key);
            }else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    //响应客户端
    private void render(HttpServletResponse response, CodeMsg cm)throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response){
        //获取两种方式的token
        String cookieToken = getCookieToken(request, MiaoshaUserService.COOKIE_NAME_TOKEN);
        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        //两种都为空返回null
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return null;
        }
        //优先取paramToken
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        //返回MiaoshaUser对象
        return miaoshaUserService.getByToken(response,token);
    }

    private String getCookieToken(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length<0){
            return null;
        }
        for(Cookie cookie:cookies) {
            if(cookieName.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }
}
