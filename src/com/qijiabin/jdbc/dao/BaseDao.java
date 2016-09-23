package com.qijiabin.jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseDao {
	
	public static final String Driver = "com.mysql.jdbc.Driver";
	public static final String URL = "jdbc:mysql://localhost:3306/my2?useUnicoded=true&characterEncoding=utf-8";
	public static final String USERNAME = "root";
	public static final String PASSWORD = "root";
	public static final String URL2 = "jdbc:mysql://localhost:3306/my?useUnicoded=true&characterEncoding=utf-8";
	public static final String USERNAME2 = "root";
	public static final String PASSWORD2 = "root";
	
	private Connection conn;
	private PreparedStatement ps;
	public ResultSet rs;
	
	public void getConn(String url, String username, String password){
		try {
			Class.forName(Driver);
			conn = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet execQuery(String sql,String[] params){
		getConn(URL2, USERNAME2, PASSWORD2);
		try {
			ps = conn.prepareStatement(sql);
			if(params!=null&&params.length>0){
				for(int i=0;i<params.length;i++){
					ps.setString(i+1, params[i]);
				}
			}
			rs = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public int execUpdate(String sql,String[] params){
		int count = 0;
		getConn(URL, USERNAME, PASSWORD);
		try {
			ps = conn.prepareStatement(sql);
			if(params!=null&&params.length>0){
				for(int i=0;i<params.length;i++){
					ps.setString(i+1, params[i]);
				}
			}
			count = ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("SQLException: " + e.getMessage());
			if (conn != null) {
					System.err.println("Transaction is being rolled back");
					try {
						conn.rollback();	
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			}
		}finally{
//			closeAll();
		}
		return count;
	}
	
	public void closeAll(){
		try {
			if(rs!=null)
				rs.close();
			if(ps!=null)
				ps.close();
			if(conn!=null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

