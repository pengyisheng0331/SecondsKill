package com.yisen.miaosha.dao;

import com.yisen.miaosha.domain.Goods;
import com.yisen.miaosha.domain.MiaoshaGoods;
import com.yisen.miaosha.domain.User;
import com.yisen.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GoodsDao {

	//查询秒杀商品信息列表
	@Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id=g.id")
	public List<GoodsVo> listGoodsVo();

	//查询秒杀商品详细信息
	@Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id=g.id where g.id=#{goodsId}")
	public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);

	//减库存
	@Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
	public int reduceStock(MiaoshaGoods g);
}
