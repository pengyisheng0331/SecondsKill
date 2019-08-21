package com.yisen.miaosha.service;

import com.yisen.miaosha.dao.GoodsDao;
import com.yisen.miaosha.dao.OrderDao;
import com.yisen.miaosha.domain.MiaoshaOrder;
import com.yisen.miaosha.domain.MiaoshaUser;
import com.yisen.miaosha.domain.OrderInfo;
import com.yisen.miaosha.redis.OrderKey;
import com.yisen.miaosha.redis.RedisService;
import com.yisen.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @description: 订单服务
 * @author: yisheng
 * @time: 2019/8/10 11:44
 */
@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    RedisService redisService;

    /**
     * @description: 获得秒杀订单
     * @param userId
     * @param goodsId
     * @return: com.yisen.miaosha.domain.MiaoshaOrder
     * @author: yisen
     * @time: 2019/8/10 22:36
     */

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
        //return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        //从缓存中查找
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, ""+userId+"_"+goodsId, MiaoshaOrder.class);
    }

    /**
     * @description: 写入订单
     * @param user
     * @param goods
     * @return: com.yisen.miaosha.domain.OrderInfo
     * @author: yisen
     * @time: 2019/8/10 22:48
     */
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        //插入order_info
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1); //pc
        orderInfo.setStatus(0); //未支付
        orderInfo.setUserId(user.getId());
        //long orderId = orderDao.insertOrderInfo(orderInfo); 不能这样写，返回值不是id
        orderDao.insertOrderInfo(orderInfo);

        //插入miaosha_order
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrder.setGoodsId(goods.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        //将MiaoshaOrder放入缓存
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, ""+user.getId()+"_"+goods.getId(), miaoshaOrder);

        return orderInfo;

    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
