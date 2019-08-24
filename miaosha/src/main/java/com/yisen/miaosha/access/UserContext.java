package com.yisen.miaosha.access;

import com.yisen.miaosha.domain.MiaoshaUser;

/**
 * @description:
 * @author: yisheng
 * @time: 2019/8/22 19:01
 */
public class UserContext {
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

    public static void setUser(MiaoshaUser user){
        userHolder.set(user);
    }

    public static MiaoshaUser getUser(){
        return userHolder.get();
    }
}
