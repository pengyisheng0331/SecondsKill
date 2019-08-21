package com.yisen.miaosha.redis;

public class OrderKey extends BasePrefix {

	public OrderKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static OrderKey getMiaoshaOrderByUidGid = new OrderKey(60,"moUG");

}
