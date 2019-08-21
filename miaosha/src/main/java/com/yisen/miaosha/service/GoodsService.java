package com.yisen.miaosha.service;

import com.yisen.miaosha.dao.GoodsDao;
import com.yisen.miaosha.domain.Goods;
import com.yisen.miaosha.domain.MiaoshaGoods;
import com.yisen.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 商品服务
 * @author: yisheng
 * @time: 2019/8/10 11:44
 */
@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    /**
     * @description: 查询商品列表
     * @param
     * @return: java.util.List<com.yisen.miaosha.vo.GoodsVo>
     * @author: yisen
     * @time: 2019/8/10 22:38
     */
    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    /**
     * @description: 查询单个商品
     * @param goodsId
     * @return: com.yisen.miaosha.vo.GoodsVo
     * @author: yisen
     * @time: 2019/8/10 22:38
     */
    public GoodsVo getGoodsVoByGoodsId(long goodsId){
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * @description: 减库存
     * @param goods
     * @return: void
     * @author: yisen
     * @time: 2019/8/10 22:39
     */
    public boolean reduceStock(GoodsVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        //g.setStockCount(goods.getGoodsStock()-1);
        int result = goodsDao.reduceStock(g);
        return result==1?true:false;
    }
}
