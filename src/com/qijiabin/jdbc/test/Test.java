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

import com.qijiabin.jdbc.dao.BaseDao;

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
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final BaseDao MWS_FOLLOW_DAO = new BaseDao(); 
	private static final BaseDao MWS_FOLLOW_DAO2 = new BaseDao();
	private static final ExecutorService SERVICE = Executors.newFixedThreadPool(30);
	
	private void start() {
		ResultSet rs = MWS_FOLLOW_DAO.execQuery(countSql, null);
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
		System.out.println("station_followed表，一共成功插入 " + count.get() + " 条数据");
	}
	
	private void work(String querySql) {
		System.out.println("执行sql: " + querySql);
		ResultSet rs = MWS_FOLLOW_DAO.execQuery(querySql, null);
		try {
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
				Future<Integer> result = SERVICE.submit(new MyThread(sqlNew, params, MWS_FOLLOW_DAO2));
				if (result.get() == 1) {
					count.incrementAndGet();
					System.out.println("插入成功。。。");
				}
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	private static class MyThread implements Callable<Integer> {
		
		private String sql;
		private String[] params;
		private BaseDao baseDao;
		
		public MyThread(String sql, String[] params, BaseDao baseDao) {
			this.sql = sql;
			this.params = params;
			this.baseDao = baseDao;
		}

		@Override
		public Integer call() throws Exception {
			int result = baseDao.execUpdate(sql, params);
			return result;
		}
		
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		new Test().start();
		long end = System.currentTimeMillis();
		System.out.println("一共耗时： " + (end - start)/1000 + " 秒");
	}
	
}
