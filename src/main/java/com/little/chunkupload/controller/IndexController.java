package com.little.chunkupload.controller;

import com.little.chunkupload.param.MultipartFileParam;
import com.little.chunkupload.pojo.ResultStatus;
import com.little.chunkupload.pojo.ResultVo;
import com.little.chunkupload.service.StorageService;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.little.chunkupload.utils.Constants;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by KelvinHuang on 18/9/26.
 */
@Controller
@RequestMapping("/index")
public class IndexController {
	private final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private StorageService storageService;


	/**
	 * 秒传判断,断点判断
	 * @param md5
	 */
	@RequestMapping(value = "/checkFileMd5", method = RequestMethod.POST)
	@ResponseBody
	public Object checkFileMd5(String md5) throws IOException {
		Object processingObj = stringRedisTemplate.opsForHash().get(Constants.FILE_UPLOAD_STATUS, md5);
		if (processingObj == null) {
			return new ResultVo(ResultStatus.NOT_UPLOAD);
		}

		String processingStr = processingObj.toString();
		boolean uploadStatus = Boolean.parseBoolean(processingStr);
		String value = stringRedisTemplate.opsForValue().get(Constants.FILE_MD5_KEY + md5);
		if (uploadStatus) {
			return new ResultVo<>(ResultStatus.FILE_EXIST, value);
		}else {
			File confFile = new File(value);
			byte[] compelteList = FileUtils.readFileToByteArray(confFile);
			LinkedList<String> missChunkList = new LinkedList<>();
			//这个判断是什么意思
			for (int i = 0; i < compelteList.length; i++) {
				if (compelteList[i] != Byte.MAX_VALUE) {
					missChunkList.add(i + "");
				}
			}
			return new ResultVo<>(ResultStatus.PART_UPLOAD, missChunkList);

		}
	}

	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity fileUpload(MultipartFileParam param, HttpServletRequest request) {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			logger.info("begin to upload file...");
			try {
				storageService.uploadFileRandomAccessFile(param);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("file upload failed...{}", param.toString());
			}
			logger.info("fileupload end...");

		}
		return ResponseEntity.ok().body("upload successful...");
	}
}
