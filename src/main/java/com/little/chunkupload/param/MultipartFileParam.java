package com.little.chunkupload.param;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by KelvinHuang on 18/10/7.
 */
public class MultipartFileParam {
	private String uid;

	private String id;

	private int chunks;

	private int chunk;

	private long size = 0L;

	private String name;

	private MultipartFile file;

	private String md5;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getChunks() {
		return chunks;
	}

	public void setChunks(int chunks) {
		this.chunks = chunks;
	}

	public int getChunk() {
		return chunk;
	}

	public void setChunk(int chunk) {
		this.chunk = chunk;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	@Override
	public String toString() {
		return "MultipartFileParam{" +
				"uid='" + uid + '\'' +
				", id='" + id + '\'' +
				", chunks=" + chunks +
				", chunk=" + chunk +
				", size=" + size +
				", name='" + name + '\'' +
				", file=" + file +
				", md5='" + md5 + '\'' +
				'}';
	}
}
