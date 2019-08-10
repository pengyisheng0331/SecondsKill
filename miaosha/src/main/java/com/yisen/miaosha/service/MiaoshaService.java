package com.yisen.miaosha.service;

import com.yisen.miaosha.domain.MiaoshaUser;
import com.yisen.miaosha.domain.OrderInfo;
import com.yisen.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 秒杀服务
 * @author: yisheng
 * @time: 2019/8/10 11:44
 */
@Service
public class MiaoshaService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    /**
     * @description: 秒杀：减库存下订单
     * @param user
     * @param goods
     * @return: com.yisen.miaosha.domain.OrderInfo
     * @author: yisen
     * @time: 2019/8/10 22:36
     */
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {

        //减库存
        goodsService.reduceStock(goods);
        //下订单 写入order_info和miaosha_order
        OrderInfo orderInfo = orderService.createOrder(user,goods);

        return orderInfo;
    }
}
