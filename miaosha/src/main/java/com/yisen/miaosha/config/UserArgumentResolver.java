package com.yisen.miaosha.config;

import com.yisen.miaosha.access.UserContext;
import com.yisen.miaosha.domain.MiaoshaUser;
import com.yisen.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: UserArgumentResolver参数解析器
 * @author: yisheng
 * @time: 2019/8/9 15:15
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    /**
     * @description: 需要对MiaoshaUser参数处理,返回true,调用resolveArgument方法
     * @param methodParameter
     * @return: boolean
     * @author: yisen
     * @time: 2019/8/9 15:33
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //支持哪些参数
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    /**
     * @description: 真正对MiaoshaUser对象参数进行处理的函数
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return: java.lang.Object
     * @author: yisen
     * @time: 2019/8/9 15:32
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
            return UserContext.getUser();
//        //获得request,response
//        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
//
//        //获取两种方式的token
//        String cookieToken = getCookieToken(request, MiaoshaUserService.COOKIE_NAME_TOKEN);
//        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
//        //两种都为空返回null
//        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
//            return null;
//        }
//        //优先取paramToken
//        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
//        //返回MiaoshaUser对象
//        return miaoshaUserService.getByToken(response,token);
    }

    /**
     * @description: 从request请求中获取cookie
     * @param request
     * @param cookieName
     * @return: java.lang.String
     * @author: yisen
     * @time: 2019/8/9 15:29
     */
//    private String getCookieToken(HttpServletRequest request, String cookieName) {
//        Cookie[] cookies = request.getCookies();
//        if(cookies == null || cookies.length<0){
//            return null;
//        }
//        for(Cookie cookie:cookies) {
//            if(cookieName.equals(cookie.getName())){
//                return cookie.getValue();
//            }
//        }
//        return null;
//    }
}
