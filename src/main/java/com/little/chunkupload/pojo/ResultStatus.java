package com.little.chunkupload.pojo;

/**
 * Created by KelvinHuang on 18/10/4.
 */
public enum ResultStatus {
	FILE_EXIST(100, "文件已存在"),

	NOT_UPLOAD(101, "该文件没有上传过"),

	PART_UPLOAD(102, "该文件已部分上传");

	private final int statusCode;

	private final String reason;

	public int getStatusCode() {
		return statusCode;
	}

	public String getReason() {
		return reason;
	}

	ResultStatus(int statusCode, String reason) {
		this.statusCode = statusCode;
		this.reason = reason;
	}
}
