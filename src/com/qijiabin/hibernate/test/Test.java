package com.qijiabin.hibernate.test;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.qijiabin.hibernate.entity.MwsFollow;

/**
 * ========================================================
 * 日 期：2016年9月23日 下午2:40:39
 * 版 本：1.0.0
 * 类说明：基于hibernate的104000条数据的同步测试
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class Test {
	
	private static final int NUM = 10000;
	private static Long count = 0L;
	private static final String COUNT_SQL = "select count(*) from mws_follow";
	private static final String SQL_OLD = "select * from mws_follow";
	private static final String FILE_ONE = "hibernate.cfg.one.xml";
	private static final String FILE_TWO = "hibernate.cfg.two.xml";
	
	private static final SessionFactory FACTORY_ONE = new Configuration().configure(FILE_ONE).buildSessionFactory();
	private static final Session SESSION_ONE = FACTORY_ONE.openSession();
	private static final SessionFactory FACTORY_TWO = new Configuration().configure(FILE_TWO).buildSessionFactory();
	private static final Session SESSION_TWO = FACTORY_TWO.openSession();
	private static final Transaction TX = SESSION_TWO.beginTransaction();
	
	private void start() {
		Object lineObj = SESSION_ONE.createSQLQuery(COUNT_SQL).uniqueResult();
		int line = Integer.parseInt(lineObj.toString());
		
		if (line < NUM) {
			work(SQL_OLD);
		} else {
			int trade = line / NUM;
			for (int i = 0; i <= trade; i++) {
				String sql = SQL_OLD + " limit " + i*NUM +"," + NUM;
				work(sql);
			}
		}
		
		TX.commit();
		SESSION_TWO.close();
		SESSION_ONE.close();
		
		System.out.println("station_followed表，一共成功插入 " + count + " 条数据");
	}
	
	@SuppressWarnings("unchecked")
	private void work(String querySql) {
		System.out.println("执行sql: " + querySql);
		Query queryOne = SESSION_ONE.createSQLQuery(querySql).addEntity(MwsFollow.class);
		List<MwsFollow> list = queryOne.list();
		
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				MwsFollow m = list.get(i);
				MwsFollow bean = new MwsFollow();
				bean.setId(m.getId());
				bean.setNickName(m.getNickName());
				bean.setSnsId(m.getSnsId());
				bean.setStationId(m.getStationId());
				bean.setFaceUrl(m.getFaceUrl());
				bean.setCreateSeconds(m.getCreateSeconds());
				bean.setCreateDate(m.getCreateDate());
				bean.setAccessPurview(m.getAccessPurview());
				
				//插入
				int result = 0;
				try {
					SESSION_TWO.save(bean);
					result = 1;
				} catch (HibernateException e) {
					e.printStackTrace();
				}
				
				if (result == 1) {
					count++;
					System.out.println("插入成功。。。");
				}
				
			}
		}
		
		// 防止内存溢出
		SESSION_TWO.flush();
		SESSION_TWO.clear();
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		new Test().start();
		long end = System.currentTimeMillis();
		System.out.println("一共耗时： " + (end - start)/1000 + " 秒");//一共耗时： 37 秒
	}
	
}

