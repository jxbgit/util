package com.jimlp.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.jimlp.util.web.http.RequestUtils;

/**
 * <p>
 * JavaEE项目中，文件上传相关工具类（所有方法的，参数列表中，表示路径的参数，默认都为以“/”开头的，相对项目根目录的路径）
 * 
 * @author JIML
 *
 */
public class MultipartFileUtils {
	
	/**
	 * <p>
	 * 保存请求头内所有文件（文件名随机）。
	 * 
	 * @param request
	 * @param saveRelativePath 文件保存路径
	 * @param filePrefixName 文件前缀名
	 * @param dSFAE 中途保存失败是否删除已保存的内容（可选参数，默认删除。）deleteSavedFileAfterException
	 * 
	 * @return 保存成功后返回 Map<文件域name属性名, 已保存的文件的文件名>。
	 * 
	 * @throws IOException 
	 */
	public static Map<String, String> saveMultipartFile(HttpServletRequest request, String saveRelativePath, String filePrefixName, boolean... dSFAE) throws IOException {
		if (request == null || saveRelativePath == null) {
			throw new IOException("fileSaveSrc(HttpServletRequest request, String path, String filePrefixName)方法 传入的 request 或  path 为 null");
		}
		if (filePrefixName == null) {
			filePrefixName = "";
		}
		saveRelativePath = saveRelativePath.replace("\\", "/");
		if (saveRelativePath.indexOf("/") != 0) {
			saveRelativePath = "/" + saveRelativePath;
		}
		MultipartHttpServletRequest multipartRequest = RequestUtils.requestTransition(request);
		// 获取表单内所有文件域(<input name="value"/>)name属性的值(value)
		Iterator<String> names = multipartRequest.getFileNames();
		// 已保存的文件对象的缓存，便于有保存失败时删除已保存的文件。
		List<File> maybeDeleteFileTemp = new ArrayList<File>();
		// 保存成功后返回 文件域name属性名 对应 已保存的文件的文件名
		Map<String, String> map = new HashMap<String, String>();
		while (names.hasNext()) {
			// 获取下一个name属性值
			String name = (String) names.next();
			// 获取文件
			MultipartFile multipartFile = multipartRequest.getFile(name);
			// 确保传入file不是空
			if (multipartFile != null && !multipartFile.getOriginalFilename().equals("")) {
				// 原始文件名
				String originalFileName = multipartFile.getOriginalFilename();
				// 获取文件后缀名
				String fileSuffixName = originalFileName.substring(originalFileName.lastIndexOf("."));
				// 生成时间字符串"yyyyMMddHHmmss"
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
				String timeStr = sdf.format(new Date());
				// 生成1000-9999之间的随机数
				int max = 10000;
				int min = 1000;
				int randomNum = new Random().nextInt(max - min) + min;
				// 重组新的文件名
				String newFileName = filePrefixName + timeStr + randomNum + fileSuffixName;
				// 文件保存目录路径
				String savePath = ProjectUtils.getProjectPath(multipartRequest) + saveRelativePath;
				// 创建文件对象
				File uploadedFile = new File(savePath, newFileName);
				// 如果硬盘没有此文件则创建文件
				if (!uploadedFile.exists()) {
					uploadedFile.mkdirs();
				}
				try {
					// 将文件写入硬盘
					multipartFile.transferTo(uploadedFile);
					maybeDeleteFileTemp.add(uploadedFile);
				} catch (IOException e) {
					// 清除已保存的文件
					if (dSFAE != null && dSFAE.length > 0 && dSFAE[0]) {
						for (File deleteFile : maybeDeleteFileTemp) {
							deleteFile.delete();
						}
					}
					throw new IOException("文件写入硬盘时出错！", e);
				}
				map.put(name, newFileName);
			}
		}
		return map;
	}

	
	/**
	 * <p>
	 * 保存请求头内所有文件（文件名使用文件域name属性名，会覆盖旧文件）。
	 * 
	 * @param request
	 * @param saveRelativePath 文件保存路径
	 * @param fileLastName 文件结尾名称
	 * 
	 * @return 保存成功后返回 Map<文件域name属性名, 已保存的文件的文件名>（中途保存失败不会清除已保存的文件，也不会恢复已覆盖的内容）。
	 * 
	 * @throws IOException 
	 */
	public static Map<String, String> saveMultipartFileUseInputTagNameValue(HttpServletRequest request, String saveRelativePath, String fileLastName) throws IOException {
		if (request == null || saveRelativePath == null) {
			throw new IOException("fileSaveSrc(HttpServletRequest request, String path, String filePrefixName)方法 传入的 request 或  path 为 null");
		}
		if (fileLastName == null) {
			fileLastName = "";
		}
		saveRelativePath = saveRelativePath.replace("\\", "/");
		if (saveRelativePath.indexOf("/") != 0) {
			saveRelativePath = "/" + saveRelativePath;
		}
		MultipartHttpServletRequest multipartRequest = RequestUtils.requestTransition(request);
		// 获取表单内所有文件域(<input name="value"/>)name属性的值(value)
		Iterator<String> names = multipartRequest.getFileNames();
		// 保存成功后返回 文件域name属性名 对应 已保存的文件的文件名
		Map<String, String> map = new HashMap<String, String>();
		while (names.hasNext()) {
			// 获取下一个name属性值
			String name = (String) names.next();
			// 获取文件
			MultipartFile multipartFile = multipartRequest.getFile(name);
			String fileName = "";
			/*if (name.contains(File.separator)) {
				fileName = name.substring(name.lastIndexOf(File.separator)+1, name.lastIndexOf("."));
			} else {
				fileName = name.substring(0, name.lastIndexOf("."));
			}*/
			fileName = name;
			// 确保传入file不是空
			if (multipartFile != null && !multipartFile.getOriginalFilename().equals("")) {
				// 原始文件名
				String originalFileName = multipartFile.getOriginalFilename();
				// 获取文件后缀名
				String fileSuffixName = originalFileName.substring(originalFileName.lastIndexOf("."));
				// 文件保存目录路径
				String savePath = ProjectUtils.getProjectPath(multipartRequest) + saveRelativePath;
				// 创建删除文件对象
				// File deleteFile = new File(savePath, name);
				// deleteFile.delete();
				// 创建保存文件对象
				File uploadedFile = new File(savePath, fileName + fileLastName + fileSuffixName);
				// 如果硬盘没有此文件则创建文件
				if (!uploadedFile.exists()) {
					uploadedFile.mkdirs();
				}
				try {
					// 将文件写入硬盘
					multipartFile.transferTo(uploadedFile);
				} catch (IOException e) {
					throw new IOException("文件写入硬盘时出错！", e);
				}
				map.put(name, fileName + fileLastName + fileSuffixName);
			}
		}
		return map;
	}
}
