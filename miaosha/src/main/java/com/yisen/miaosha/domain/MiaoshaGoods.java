package com.yisen.miaosha.domain;

import java.util.Date;

/**
 * @description: 秒杀商品表
 * @author: yisheng
 * @time: 2019/8/10 11:18
 */
public class MiaoshaGoods {

    private Long id;
    private Long goodsId; //商品id
    private Double miaoshaPrice; //秒杀价
    private Integer stockCount; //库存数量
    private Date startDate; //秒杀开始时间
    private Date endDate; //秒杀结束时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Double getMiaoshaPrice() {
        return miaoshaPrice;
    }

    public void setMiaoshaPrice(Double miaoshaPrice) {
        this.miaoshaPrice = miaoshaPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
