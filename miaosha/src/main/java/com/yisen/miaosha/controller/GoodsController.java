package com.yisen.miaosha.controller;

import com.yisen.miaosha.domain.MiaoshaUser;
import com.yisen.miaosha.redis.GoodsKey;
import com.yisen.miaosha.redis.RedisService;
import com.yisen.miaosha.result.Result;
import com.yisen.miaosha.service.GoodsService;
import com.yisen.miaosha.service.MiaoshaUserService;
import com.yisen.miaosha.vo.GoodsDetailVo;
import com.yisen.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
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

	@Autowired
	private ThymeleafViewResolver thymeleafViewResolver;

	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * QPS:1267 load:15 mysql
	 * 5000 * 10
	 * QPS:2884, load:5
	 * */
	/**
	 * @description: 商品列表 + 页面缓存  QPS:355
	 * @param request
	 * @param response
	 * @param user
	 * @param model
	 * @return: java.lang.String
	 * @author: yisen
	 * @time: 2019/8/15 14:27
	 */
    @RequestMapping(value="/to_list", produces="text/html")
	@ResponseBody
	public String list(HttpServletRequest request,HttpServletResponse response,
					   MiaoshaUser user, Model model) {
		//1.先取缓存
		String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
		if(!StringUtils.isEmpty(html)){
			return html;
		}

		//2.缓存没有，查询秒杀商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("user", user);
		model.addAttribute("goodsList", goodsList);

		//SpringWebContext
		SpringWebContext ctx = new SpringWebContext(request,response,
				request.getServletContext(),request.getLocale(),model.asMap(),applicationContext);
		//3.手动渲染
		html = thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
		if(!StringUtils.isEmpty(html)){
			redisService.set(GoodsKey.getGoodsList,"",html);
		}
		return html;

    }

//	/**
//	 * @description: 使用页面缓存之前的商品列表方法   2000*5 QPS:240
//	 * @param user
//	 * @param model
//	 * @return: java.lang.String
//	 * @author: yisen
//	 * @time: 2019/8/9 14:43
//	 */
//	@RequestMapping("/to_list")
//	public String list(MiaoshaUser user,Model model) {
//		//查询秒杀商品列表
//		List<GoodsVo> goodsList = goodsService.listGoodsVo();
//		model.addAttribute("user", user);
//		model.addAttribute("goodsList", goodsList);
//		return "goods_list";
//	}

	/**
	 * @description: 商品详情 页面静态化
	 * @param request
	 * @param response
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return: java.lang.String
	 * @author: yisen
	 * @time: 2019/8/15 14:28
	 */
	@RequestMapping(value="/detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
										@PathVariable("goodsId")long goodsId) {

		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if(now < startAt ) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int)((startAt - now )/1000);
		}else  if(now > endAt){//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}

		//封装GoodsDetailVo
		GoodsDetailVo vo = new GoodsDetailVo();
		vo.setGoods(goods);
		vo.setUser(user);
		vo.setRemainSeconds(remainSeconds);
		vo.setMiaoshaStatus(miaoshaStatus);
		return Result.success(vo);
	}
//    /**
//     * @description: 商品详情页
//     * @param user
//     * @param model
//     * @param goodsId
//     * @return: java.lang.String
//     * @author: yisen
//     * @time: 2019/8/10 16:26
//     */
//    @RequestMapping("/to_detail/{goodsId}")
//	public String detail(MiaoshaUser user, Model model,
//						 @PathVariable("goodsId")long goodsId){
//    	//snowflake
//		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//		model.addAttribute("goods", goods);
//
//		long startAt = goods.getStartDate().getTime();
//		long endAt = goods.getEndDate().getTime();
//		long now = System.currentTimeMillis();
//
//		int miaoshaStatus = 0;
//		int remainSeconds = 0;
//
//		if(now < startAt){     //秒杀没开始，倒计时
//			miaoshaStatus = 0;
//			remainSeconds = (int)((startAt-now)/1000);
//		}else if(now > endAt){ //秒杀已经结束
//			miaoshaStatus = 2;
//			remainSeconds = -1;
//		}else{                 //秒杀进行中
//			miaoshaStatus = 1;
//			remainSeconds = 0;
//		}
//
//		model.addAttribute("user", user);
//		model.addAttribute("miaoshaStatus",miaoshaStatus);
//		model.addAttribute("remainSeconds",remainSeconds);
//
//		return "goods_detail";
//	}
    
}
