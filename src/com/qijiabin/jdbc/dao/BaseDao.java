package com.qijiabin.jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseDao {
	
	public static final String Driver = "com.mysql.jdbc.Driver";
	private String username;
	private String password;
	private String url;
	private Connection conn;
	private PreparedStatement ps;
	public ResultSet rs;
	
	public BaseDao(String username, String password, String url) {
		this.username = username;
		this.password = password;
		this.url = url;
	}
	
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
		getConn(url, username, password);
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
		getConn(url, username, password);
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

