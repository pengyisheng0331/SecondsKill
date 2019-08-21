package com.yisen.miaosha.service;


import com.yisen.miaosha.dao.MiaoshaUserDao;
import com.yisen.miaosha.domain.MiaoshaUser;
import com.yisen.miaosha.exception.GlobalException;
import com.yisen.miaosha.redis.MiaoshaUserKey;
import com.yisen.miaosha.redis.RedisService;
import com.yisen.miaosha.result.CodeMsg;
import com.yisen.miaosha.util.MD5Util;
import com.yisen.miaosha.util.UUIDUtil;
import com.yisen.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {
	
	
	public static final String COOKIE_NAME_TOKEN = "token";
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	RedisService redisService;

	/**
	 * @description: 通过id获取MiaoshaUser对象
	 * @param id
	 * @return: com.yisen.miaosha.domain.MiaoshaUser
	 * @author: yisen
	 * @time: 2019/8/9 14:50
	 */
	public MiaoshaUser getById(long id) {
		//取缓存
		MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, ""+id, MiaoshaUser.class);
		if(user != null) {
			return user;
		}
		//取数据库
		user = miaoshaUserDao.getById(id);
		if(user != null) {
			redisService.set(MiaoshaUserKey.getById, ""+id, user);
		}
		return user;
	}

	// http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
	public boolean updatePassword(String token, long id, String formPass) {
		//取user
		MiaoshaUser user = getById(id);
		if(user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//更新数据库
		MiaoshaUser toBeUpdate = new MiaoshaUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
		miaoshaUserDao.update(toBeUpdate);
		//处理缓存 删除redis缓存
		redisService.delete(MiaoshaUserKey.getById, ""+id);
		user.setPassword(toBeUpdate.getPassword());
		//更新token
		redisService.set(MiaoshaUserKey.token, token, user);
		return true;
	}

	/**
	 * @description: 在redis中查找token，通过token获取MiaoshaUser对象
	 * @param response
	 * @param token
	 * @return: com.yisen.miaosha.domain.MiaoshaUser
	 * @author: yisen
	 * @time: 2019/8/9 14:47
	 */
	public MiaoshaUser getByToken(HttpServletResponse response, String token) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);

		if(user != null) {
			addCookie(response, token, user); //延长有效期
		}
		return user;
	}


	/**
	 * @description: 登录逻辑，判断手机号密码，生成cookie
	 * @param response
	 * @param loginVo
	 * @return: boolean
	 * @author: yisen
	 * @time: 2019/8/9 14:49
	 */
	public String login(HttpServletResponse response, LoginVo loginVo) {
		if(loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		//判断手机号是否存在
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if(user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//验证密码
		String dbPass = user.getPassword();
		String saltDB = user.getSalt();
		String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
		if(!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		//生成cookie，同时保存到redis
		String token= UUIDUtil.uuid();
		addCookie(response, token, user);
		return token;
	}


	/**
	 * @description: 分布式session,session信息保存到redis,cookie信息保存到用户本地
	 * @param response
	 * @param token
	 * @param user
	 * @return: void
	 * @author: yisen
	 * @time: 2019/8/9 14:51
	 */
	private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
		redisService.set(MiaoshaUserKey.token, token, user); //保存到redis
		Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie); //保存到cookie
	}

}
