package com.yisen.miaosha.controller;

import com.yisen.miaosha.domain.MiaoshaOrder;
import com.yisen.miaosha.domain.MiaoshaUser;
import com.yisen.miaosha.domain.OrderInfo;
import com.yisen.miaosha.redis.RedisService;
import com.yisen.miaosha.result.CodeMsg;
import com.yisen.miaosha.service.GoodsService;
import com.yisen.miaosha.service.MiaoshaService;
import com.yisen.miaosha.service.MiaoshaUserService;
import com.yisen.miaosha.service.OrderService;
import com.yisen.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description: 秒杀
 * @author: yisheng
 * @time: 2019/8/10 22:02
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    private MiaoshaUserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MiaoshaService miaoshaService;

    /**
     * @description: 商品秒杀 QPS:286
     * @param user
     * @param model
     * @param goodsId
     * @return: java.lang.String
     * @author: yisen
     * @time: 2019/8/10 22:37
     */
    @RequestMapping("/do_miaosha")
    public String miaosha(MiaoshaUser user, Model model,
                       @RequestParam("goodsId")long goodsId) {
        //用户是否登录
        if(user == null){
            return "login";
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock < 0){
            model.addAttribute("errorMsg", CodeMsg.MIAOSHA_OVER.getMsg());
            return "miaosha_fail";
        }

        //判断是否已经秒杀到
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(),goodsId);
        if(miaoshaOrder != null){
            model.addAttribute("errorMsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }

        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user,goods);

        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);

        return "order_detail";
    }
}
