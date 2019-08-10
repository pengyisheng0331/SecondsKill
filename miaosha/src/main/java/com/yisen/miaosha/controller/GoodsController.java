package com.yisen.miaosha.controller;

import com.yisen.miaosha.domain.MiaoshaUser;
import com.yisen.miaosha.redis.RedisService;
import com.yisen.miaosha.service.GoodsService;
import com.yisen.miaosha.service.MiaoshaUserService;
import com.yisen.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	private MiaoshaUserService userService;
	
	@Autowired
	private RedisService redisService;

	@Autowired
	private GoodsService goodsService;


	/**
	 * @description: 商品列表页
	 * @param user
	 * @param model
	 * @return: java.lang.String
	 * @author: yisen
	 * @time: 2019/8/9 14:43
	 */
    @RequestMapping("/to_list")
    public String list(MiaoshaUser user,Model model) {
    	//查询秒杀商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("user", user);
		model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    
}
