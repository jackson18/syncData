package com.qijiabin.hibernate.entity;

import java.util.Date;

public class MwsFollow {

	private long id;
	
	private long stationId;
	
	private long snsId;
	
	private String nickName;
	
	private String faceUrl;
	
	private int accessPurview;
	
	private long createSeconds;
	
	private Date createDate;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getStationId() {
		return stationId;
	}
	public void setStationId(long stationId) {
		this.stationId = stationId;
	}
	public long getSnsId() {
		return snsId;
	}
	public void setSnsId(long snsId) {
		this.snsId = snsId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getFaceUrl() {
		return faceUrl;
	}
	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}
	public int getAccessPurview() {
		return accessPurview;
	}
	public void setAccessPurview(int accessPurview) {
		this.accessPurview = accessPurview;
	}
	public long getCreateSeconds() {
		return createSeconds;
	}
	public void setCreateSeconds(long createSeconds) {
		this.createSeconds = createSeconds;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
