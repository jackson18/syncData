package com.qijiabin.jdbc.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.qijiabin.jdbc.dao.BaseDao;
import com.qijiabin.jdbc.dao.ConnectionFactory;

/**
 * ========================================================
 * 日 期：2016年9月23日 下午2:40:39
 * 版 本：1.0.0
 * 类说明：基于jdbc的104000条数据的同步测试
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class Test {

	private static final int num = 10000;
	private static AtomicLong count = new AtomicLong(0);
	private static final String countSql = "select count(*) from mws_follow";
	private static final String sqlOld = "select * from mws_follow";
	private static final String sqlNew = "insert into mws_follow(id, station_id, sns_id, nick_name, face_url, access_purview, create_milliseconds, create_date) values(?,?,?,?,?,?,?,?)";
	
	public static final String URL = "jdbc:mysql://localhost:3306/my2?useUnicoded=true&characterEncoding=utf-8";
	public static final String USERNAME = "root";
	public static final String PASSWORD = "root";
	public static final String URL2 = "jdbc:mysql://localhost:3306/my?useUnicoded=true&characterEncoding=utf-8";
	public static final String USERNAME2 = "root";
	public static final String PASSWORD2 = "root";
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final ExecutorService SERVICE = Executors.newFixedThreadPool(30);
	private static GenericObjectPool<BaseDao> pool;
	private static GenericObjectPool<BaseDao> pool2;
	
	public Test() {
		// 创建池对象工厂
		PooledObjectFactory<BaseDao> factory = new ConnectionFactory(USERNAME, PASSWORD, URL);
		PooledObjectFactory<BaseDao> factory2 = new ConnectionFactory(USERNAME2, PASSWORD2, URL2);
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxIdle(5);
		poolConfig.setMinIdle(1);
		poolConfig.setMaxTotal(35);
		
		// 创建对象池
		pool = new GenericObjectPool<BaseDao>(factory, poolConfig);  
		pool2 = new GenericObjectPool<BaseDao>(factory2, poolConfig);  
	}
	
	
	private void start() {
		try {
			BaseDao baseDao = pool2.borrowObject();
			ResultSet rs = baseDao.execQuery(countSql, null);
			try {
				int line = 0;
				while (rs.next()) {
					line = rs.getInt(1);
				}
				
				if (line < num) {
					work(sqlOld);
				} else {
					int trade = line / num;
					for (int i = 0; i <= trade; i++) {
						String sql = sqlOld + " limit " + i*num +"," + num;
						work(sql);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			pool2.returnObject(baseDao);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("station_followed表，一共成功插入 " + count.get() + " 条数据");
	}
	
	private void work(String querySql) {
		System.out.println("执行sql: " + querySql);
		try {
			BaseDao baseDao = pool2.borrowObject();
			ResultSet rs = baseDao.execQuery(querySql, null);
			
			while(rs.next()){
				long id = rs.getLong("id");
				long station_id = rs.getLong("station_id");
				long sns_id = rs.getInt("sns_id");
				String nick_name = rs.getString("nick_name");
				String face_url = rs.getString("face_url");
				int access_purview = rs.getInt("access_purview");
				long create_milliseconds = rs.getLong("create_milliseconds");
				Date create_date = rs.getDate("create_date");
				
				// 转换
				String createTime = sdf.format(create_date);
				
				// 插入
				String[] params = {id+"", station_id+"", sns_id+"", nick_name, face_url, access_purview+"", create_milliseconds+"", createTime};
				Future<Integer> result = SERVICE.submit(new MyThread(sqlNew, params));
				if (result.get() == 1) {
					count.incrementAndGet();
					System.out.println("插入成功。。。");
				}
			}
			
			pool2.returnObject(baseDao);
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class MyThread implements Callable<Integer> {
		
		private String sql;
		private String[] params;
		
		public MyThread(String sql, String[] params) {
			this.sql = sql;
			this.params = params;
		}

		@Override
		public Integer call() throws Exception {
			BaseDao baseDao = pool.borrowObject();
			int result = baseDao.execUpdate(sql, params);
			pool.returnObject(baseDao);
			return result;
		}
		
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		new Test().start();
		long end = System.currentTimeMillis();
		System.out.println("一共耗时： " + (end - start)/1000 + " 秒");//一共耗时： 1141 秒
	}
	
}

