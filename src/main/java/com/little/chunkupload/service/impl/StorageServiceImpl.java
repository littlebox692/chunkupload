package com.little.chunkupload.service.impl;

import com.little.chunkupload.param.MultipartFileParam;
import com.little.chunkupload.service.StorageService;
import com.little.chunkupload.utils.Constants;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by KelvinHuang on 18/10/7.
 */
@Service
public class StorageServiceImpl implements StorageService {
	private final static Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

	private Path rootPath;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Value("${breakpoint.upload.chunkSize}")
	private long CHUNK_SIZE;

	@Value("${breakpoint.upload.dir}")
	private String finalDirPath;

	public StorageServiceImpl(@Value("${breakpoint.upload.dir}") String location) {
		logger.info("=============test location: " + location);

		this.rootPath = Paths.get(location);
	}

	@Override
	public void deleteAll() {
		logger.info("begin to clear data...");
		FileSystemUtils.deleteRecursively(rootPath.toFile());
		stringRedisTemplate.delete(Constants.FILE_UPLOAD_STATUS);
		stringRedisTemplate.delete(Constants.FILE_MD5_KEY);
		logger.info("finish clearing data...");
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootPath);
		} catch (FileAlreadyExistsException e) {
			logger.error("dir already exist, there is no need to create again...", e);
		} catch (IOException e) {
			logger.error("init create root dir failed...", e);
		}

	}

	@Override
	public void uploadFileRandomAccessFile(MultipartFileParam param) throws IOException {
		String fileName = param.getName();
		String tempDirPath = finalDirPath + param.getMd5();
		String tempFileName = fileName + "_tmp";
		File tempDir = new File(tempDirPath);
		File tempFile = new File(tempDirPath, tempFileName);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		RandomAccessFile accessTmpFile = new RandomAccessFile(tempFile, "rw");
		long offset = CHUNK_SIZE * param.getChunk();
		accessTmpFile.seek(offset);
		accessTmpFile.write(param.getFile().getBytes());
		accessTmpFile.close();

		boolean isOk = checkAndSetUploadProgress(param, tempDirPath);
		if (isOk) {
			boolean flag = renameFile(tempFile, fileName);
			logger.info("upload compelete! " + flag + " name = " + fileName);
		}
	}

	private boolean checkAndSetUploadProgress(MultipartFileParam param, String uploadDirPath) throws IOException {
		String fileName = param.getName();
		File confFile = new File(uploadDirPath, fileName + ".conf");
		RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");
		logger.info("set part " + param.getChunk() + " complete...");
		accessConfFile.setLength(param.getChunks());
		accessConfFile.seek(param.getChunk());
		accessConfFile.write(Byte.MAX_VALUE);

		byte[] completeList = FileUtils.readFileToByteArray(confFile);
		byte isComplete = Byte.MAX_VALUE;
		for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
			isComplete = (byte)(isComplete & completeList[i]);
			logger.info("check part " + i + " complete?: " + completeList[i]);
		}
		accessConfFile.close();
		if (isComplete == Byte.MAX_VALUE) {
			stringRedisTemplate.opsForHash().put(Constants.FILE_UPLOAD_STATUS, param.getMd5(), "ture");
			stringRedisTemplate.opsForValue().set(Constants.FILE_MD5_KEY + param.getMd5(), uploadDirPath + "/" + fileName);
			return true;
		}else {
			if (!stringRedisTemplate.hasKey(Constants.FILE_MD5_KEY + param.getMd5())) {
				stringRedisTemplate.opsForValue().set(Constants.FILE_MD5_KEY + param.getMd5(), uploadDirPath + "/" + fileName + ".conf");
			}
			return false;
		}
	}

	public boolean renameFile(File toBeRenamed, String toFileNewName) {
		if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
			logger.info("File does not exist: " + toBeRenamed.getName());
			return false;
		}
		String parent = toBeRenamed.getParent();
		File newFile = new File(parent + File.separatorChar + toFileNewName);
		return toBeRenamed.renameTo(newFile);
	}
}
