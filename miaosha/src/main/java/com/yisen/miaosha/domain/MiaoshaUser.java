package com.yisen.miaosha.domain;

import java.util.Date;

public class MiaoshaUser {
/*	CREATE TABLE `miaosha_user` (
			`id` bigint(20) NOT NULL COMMENT '用户id,手机号码',
			`nickname` varchar(255) NOT NULL COMMENT '昵称',
			`password` varchar(32) DEFAULT NULL COMMENT '密码，两次md5',
			`salt` varchar(10) DEFAULT NULL COMMENT '随机值',
			`head` varchar(128) DEFAULT NULL COMMENT '头像，云存储的id',
			`register_date` datetime DEFAULT NULL COMMENT '注册时间',
			`last_login_date` datetime DEFAULT NULL COMMENT '上次登录时间',
			`login_count` int(11) DEFAULT NULL COMMENT '登录次数',
	PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;*/

	private Long id;         //用户id,手机号码
	private String nickname; //昵称
	private String password; //密码，两次md5
	private String salt;     //随机值
	private String head;     //头像，云存储的id
	private Date registerDate;  //注册时间
	private Date lastLoginDate; //上次登录时间
	private Integer loginCount; //登录次数

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public Integer getLoginCount() {
		return loginCount;
	}
	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}
}
