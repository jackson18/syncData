package com.qijiabin.jdbc.dao;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ConnectionFactory extends BasePooledObjectFactory<BaseDao> {
	
	private String username;
	private String password;
	private String url;
	
	public ConnectionFactory(String username, String password, String url) {
		this.username = username;
		this.password = password;
		this.url = url;
	}

	@Override
	public BaseDao create() throws Exception {
		return new BaseDao(username, password, url);
	}

	@Override
	public PooledObject<BaseDao> wrap(BaseDao baseDao) {
		return new DefaultPooledObject<BaseDao>(baseDao);
	}

}
