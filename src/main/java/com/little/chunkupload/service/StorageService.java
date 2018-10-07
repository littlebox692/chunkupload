package com.little.chunkupload.service;

import com.little.chunkupload.param.MultipartFileParam;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by KelvinHuang on 18/9/25.
 */
public interface StorageService {

	void deleteAll();

	void init();

	void uploadFileRandomAccessFile(MultipartFileParam param) throws IOException;

}
