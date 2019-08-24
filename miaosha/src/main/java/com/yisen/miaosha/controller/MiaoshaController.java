package com.yisen.miaosha.controller;

import com.yisen.miaosha.access.AccessLimit;
import com.yisen.miaosha.domain.MiaoshaOrder;
import com.yisen.miaosha.domain.MiaoshaUser;
import com.yisen.miaosha.domain.OrderInfo;
import com.yisen.miaosha.rabbitmq.MQSender;
import com.yisen.miaosha.rabbitmq.MiaoshaMessage;
import com.yisen.miaosha.redis.AccessKey;
import com.yisen.miaosha.redis.GoodsKey;
import com.yisen.miaosha.redis.RedisService;
import com.yisen.miaosha.result.CodeMsg;
import com.yisen.miaosha.result.Result;
import com.yisen.miaosha.service.GoodsService;
import com.yisen.miaosha.service.MiaoshaService;
import com.yisen.miaosha.service.MiaoshaUserService;
import com.yisen.miaosha.service.OrderService;
import com.yisen.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * @description: 秒杀
 * @author: yisheng
 * @time: 2019/8/10 22:02
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

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

    @Autowired
    MQSender sender;

    private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();

    @Override
    public void afterPropertiesSet() throws Exception {   //初始化库存，标记商品秒杀over为false
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null){
            return;
        }
        for(GoodsVo goods:goodsList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }

    /**
     * QPS:1306  2114
     * 5000 * 10
     * */
    /**
     *  GET POST有什么区别？
     * */
    @RequestMapping(value="/{path}/do_miaosha", method=RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model,MiaoshaUser user,
                                   @RequestParam("goodsId")long goodsId,
                                    @PathVariable("path")String path) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //验证path
        boolean check = miaoshaService.checkPath(user, goodsId, path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if(over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //1.预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);
        if(stock < 0) {
            localOverMap.put(goodsId, true); //true表示商品已秒杀完，后续访问不需要再访问redis
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //2.判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //3.入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);
        return Result.success(0);//立即返回排队中

//        //判断库存
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
//        int stock = goods.getStockCount();
//        if(stock <= 0) {
//            return Result.error(CodeMsg.MIAO_SHA_OVER);
//        }
//        //判断是否已经秒杀到了
//        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//        if(order != null) {
//            return Result.error(CodeMsg.REPEATE_MIAOSHA);
//        }
//        //减库存 下订单 写入秒杀订单
//        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
//        return Result.success(orderInfo);
    }


    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request,
                                         MiaoshaUser user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value="verifyCode", defaultValue="0")int verifyCode
    ) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
//        //限流 , 查询访问次数
//        String uri = request.getRequestURI();
//        String key = uri + "_" + user.getId();
//        Integer count = redisService.get(AccessKey.access, key, Integer.class);
//        if(count == null){
//            redisService.set(AccessKey.access,key,1);
//        }else if(count < 5){
//            redisService.incr(AccessKey.access,key);
//        }else{
//            return  Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
//        }

        //校验验证码
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //生成秒杀接口
        String path  = miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response, MiaoshaUser user,
                                              @RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }


//    /**
//     * @description: 商品秒杀 QPS:286
//     * @param user
//     * @param model
//     * @param goodsId
//     * @return: java.lang.String
//     * @author: yisen
//     * @time: 2019/8/10 22:37
//     */
//    @RequestMapping("/do_miaosha")
//    public String miaosha(MiaoshaUser user, Model model,
//                       @RequestParam("goodsId")long goodsId) {
//        //用户是否登录
//        if(user == null){
//            return "login";
//        }
//        //判断库存
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        int stock = goods.getStockCount();
//        if(stock < 0){
//            model.addAttribute("errorMsg", CodeMsg.MIAOSHA_OVER.getMsg());
//            return "miaosha_fail";
//        }
//
//        //判断是否已经秒杀到
//        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(),goodsId);
//        if(miaoshaOrder != null){
//            model.addAttribute("errorMsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
//            return "miaosha_fail";
//        }
//
//        //减库存 下订单 写入秒杀订单
//        OrderInfo orderInfo = miaoshaService.miaosha(user,goods);
//
//        model.addAttribute("orderInfo",orderInfo);
//        model.addAttribute("goods",goods);
//
//        return "order_detail";
//    }
}
