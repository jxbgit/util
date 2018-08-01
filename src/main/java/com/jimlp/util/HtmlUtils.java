package com.jimlp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jimlp.util.file.FileUtils;

/**
 * <p>
 * HTML处理的相关工具类<br>
 * 未特殊说明的路径，默认都是以“/”开头的相对项目根目录的路径
 * 
 * @author JIML
 *
 */
public class HtmlUtils {

	public static String separator = "/";

	/**
	 * <p>
	 * 用于提取和解析成标准的 HTML Document 对象。
	 * 
	 * @param requestUrl
	 *            连接到的 URL ，该协议必须是 HTTP 或 HTTPS 。
	 * 
	 * @return 标准的 HTML Document 对象
	 * 
	 * @throws IOException
	 */
	public static Document getHTMLDom(String requestUrl) throws IOException {
		Document doc = Jsoup.connect(requestUrl).get();
		return doc;
	}

	/**
	 * <p>
	 * 用于提取和解析成标准的 HTML 字符串。
	 * 
	 * @param requestUrl
	 *            连接到的 URL ，该协议必须是 HTTP 或 HTTPS 。
	 * 
	 * @return 标准的 HTML 字符串
	 * 
	 * @throws IOException
	 */
	public static String getHTMLDomString(String requestUrl) throws IOException {
		Document doc = Jsoup.connect(requestUrl).get();
		return doc.toString();
	}

	// 服务于 updateSrc 方法

	// 正则，匹配字符串中以“/”开头，以“/”结尾且中间不包含“"”、“.”和“任意空格”的部分
	// （匹配“ /a/b.jpg ”中的“ /a/ ”）
	private static String regex = "/([^\".\\s]+/)?";
	// 正则，匹配字符串中以“ src ”开头且其值除前后空格以“ / ”开头到最后一个“ / ”
	// （匹配“ <img src="/a/b.jpg"/> ” 中的 “ src="/a/ ”）
	private static String regex2 = "(src){1}\\s*=\\s*(\")\\s*/([^\".\\s]+/)?";

	/**
	 * <p>
	 * 更新 html 文本内媒体标签（img、audio、video 等）标签中 src
	 * 的值为以“/”开头的相对项目根目录的src地址，并将媒体文件移动到新路径。
	 * 
	 * @param html
	 *            要处理的 HTML 文本
	 * @param destFolder
	 *            媒体文件重新保存的目录（example：/a/b/
	 *            ，如果目录前后不加“/”，会自动前后添加“/”。如果目录为空则默认保存到项目根目录。）
	 * 
	 * @return 操作成功后返回更新后的 html 文本内容
	 * 
	 * @throws IOException
	 */
	public static String updateSrc(String html, String destFolder) throws IOException {
		if (html == null) {
			return null;
		}
		if (destFolder == null || destFolder.trim().length() == 0) {
			destFolder = separator;
		}
		if (!destFolder.startsWith(separator)) {
			destFolder = separator + destFolder;
		}
		if (!destFolder.endsWith(separator)) {
			destFolder += separator;
		}
		String projectPath = ProjectUtils.getProjectPath();
		List<String> srcUrlList = HtmlUtils.listRelativePathSrcUrl(html);
		for (String srcUrl : srcUrlList) {
			String newSrcURL = srcUrl.replaceAll(regex, destFolder);
			if (!srcUrl.equals(newSrcURL)) {
				FileUtils.copyFile(projectPath + srcUrl, projectPath + newSrcURL);
				FileUtils.upDelete(srcUrl);
			}
		}
		html = html.replaceAll(regex2, "src=\"" + destFolder);
		return html;
	}

	/**
	 * <p>
	 * 对比 oldHtml 更新 newHtml 文本内新增的媒体标签（img、audio、video 等）标签中 src
	 * 的值为以“/”开头的相对项目根目录的src地址，并将媒体文件移动到新路径。<br>
	 * 同时删除对应已删除的媒体标签的媒体文件
	 * 
	 * @param oldHtml
	 *            用于对比的老 HTML 文本
	 * @param newHtml
	 *            要处理的 HTML 文本
	 * @param destFolder
	 *            媒体文件重新保存的目录（example：/a/b/
	 *            ，如果目录前后不加“/”，会自动前后添加“/”。如果目录为空则默认保存到项目根目录。）
	 * 
	 * @return 操作成功后返回更新后的 newHtml 文本内容
	 * 
	 * @throws IOException
	 */
	public static String updateSrc(String oldHtml, String newHtml, String destFolder) throws IOException {
		if (oldHtml == null && newHtml == null) {
			return null;
		}
		String projectPath = ProjectUtils.getProjectPath();
		// 新内容 == null，旧内容必 != null
		if (newHtml == null) {
			List<String> oldSrcUrlList = HtmlUtils.listRelativePathSrcUrl(oldHtml);
			for (String oldSrcUrl : oldSrcUrlList) {
				FileUtils.upDelete(projectPath + oldSrcUrl);
			}
			return null;
		}
		List<String> newSrcUrlList = HtmlUtils.listRelativePathSrcUrl(newHtml);
		if (oldHtml != null) {
			if (destFolder == null || destFolder.trim().length() == 0) {
				destFolder = separator;
			}
			if (!destFolder.startsWith(separator)) {
				destFolder = separator + destFolder;
			}
			if (!destFolder.endsWith(separator)) {
				destFolder += separator;
			}
			newHtml = newHtml.replaceAll(regex2, "src=\"" + destFolder);
			List<String> oldSrcUrlList = HtmlUtils.listRelativePathSrcUrl(oldHtml);
			for (String oldSrcUrl : oldSrcUrlList) {
				if (!newSrcUrlList.contains(oldSrcUrl)) {
					FileUtils.upDelete(projectPath + oldSrcUrl);
				}
			}
		}
		for (String newSrcUrl : newSrcUrlList) {
			String newNewSrcUrl = newSrcUrl.replaceAll(regex, destFolder);
			if (!newSrcUrl.equals(newNewSrcUrl)) {
				FileUtils.copyFile(projectPath + newSrcUrl, projectPath + newNewSrcUrl);
				FileUtils.upDelete(newSrcUrl);
			}
		}
		return newHtml;
	}

	/**
	 * <p>
	 * 删除 html 文本内媒体标签（img、audio、video 等）中在本地存在但除排除指定文件夹外的文件。<br>
	 * 
	 * @param html
	 *            用于查找被删除文件的 HTML 文本
	 * @param ignoreFolders
	 *            忽略删除的文件夹（可选参数，可以多个）
	 * 
	 * @return 返回一个长度为 2 的数组，{删除成功的数量,删除失败的数量}
	 */
	public static int[] deleteFileOfSrc(String html, String... ignoreFolders) {
		int success = 0;
		int error = 0;
		if (html != null) {
			String projectPath = ProjectUtils.getProjectPath();
			List<String> srcUrlList = HtmlUtils.listRelativePathSrcUrl(html, ignoreFolders);
			for (String srcUrl : srcUrlList) {
				if (FileUtils.upDelete(projectPath + srcUrl)) {
					success++;
				} else {
					error++;
				}
			}
		}
		int[] array = { success, error };
		return array;
	}

	/**
	 * <p>
	 * 删除 html 文本内媒体标签（img、audio、video 等）中在本地存在但除排除指定文件夹外的文件。<br>
	 * 
	 * @param html
	 *            用于查找被删除文件的 HTML 文本
	 * @param ignoreFolderList
	 *            忽略删除的文件夹
	 * 
	 * @return 返回一个长度为 2 的数组，{删除成功的数量,删除失败的数量}
	 */
	public static int[] deleteFileOfSrc(String html, List<String> ignoreFolderList) {
		if (ignoreFolderList == null || ignoreFolderList.size() == 0) {
			return deleteFileOfSrc(html);
		}
		String[] ignoreFolders = ignoreFolderList.toArray(new String[0]);
		return deleteFileOfSrc(html, ignoreFolders);
	}

	/**
	 * <p>
	 * 获取 src 其值除前后空格以“文件名.扩展名”结尾的值列表<br>
	 * <p>
	 * 服务于 updateHTMLOfImgSrcAndSaveImagesAgain 方法
	 * 
	 * @param html
	 *            查找的内容
	 * 
	 * @return example：<br>
	 *         查找的 html 为：<img src="  http://www.jimlp.com/a/b.jpg "/><br>
	 *         返回：“http://www.jimlp.com/a/b.jpg”
	 */
	public static List<String> listSrcUrl(String html) {
		String imgTagRegex = "(src){1}\\s*=\\s*(\")\\s*[^\".]*[^/\".\\s]+\\.[a-zA-Z]+";
		Pattern imgTagPattern = Pattern.compile(imgTagRegex, Pattern.CASE_INSENSITIVE);
		List<String> list = new ArrayList<String>();
		Matcher matcher = imgTagPattern.matcher(html);
		while (matcher.find()) {
			String s = matcher.group();
			s = s.substring(s.indexOf("src"));
			s = s.substring(s.indexOf("\""));
			s = s.trim();
			list.add(s);
		}
		return list;
	}

	/**
	 * <p>
	 * 获取 src 其值除前后空格以“/”开头，以“文件名.扩展名 ”结尾，但除排除指定目录外的值列表<br>
	 * <p>
	 * 服务于 updateHTMLOfImgSrcAndSaveImagesAgain 方法
	 * 
	 * @param html
	 *            查找的内容
	 * @param ignoreFolders
	 *            忽略删除的文件夹
	 * 
	 * @return example：<br>
	 *         查找的 html 为：<img src=" /a/b.jpg "/><img src=" c/d/e.jpg "/><br>
	 *         返回：“/a/b.jpg”
	 */
	public static List<String> listRelativePathSrcUrl(String html, String... ignoreFolders) {
		String imgTagRegex = "(src){1}\\s*=\\s*\"\\s*";
		String flag = "";
		if (ignoreFolders != null && ignoreFolders.length > 0) {
			imgTagRegex += "(?!\\s*";
			for (String ignoreFolder : ignoreFolders) {
				if (!ignoreFolder.startsWith(separator)) {
					ignoreFolder = separator + ignoreFolder;
				}
				if (!ignoreFolder.endsWith(separator)) {
					ignoreFolder += separator;
				}
				imgTagRegex += flag + ignoreFolder;
				flag = "|";
			}
			imgTagRegex += ")";
		}
		imgTagRegex += "[^\".]*\\.[a-zA-Z]+";
		Pattern imgTagPattern = Pattern.compile(imgTagRegex, Pattern.CASE_INSENSITIVE);
		List<String> list = new ArrayList<String>();
		Matcher matcher = imgTagPattern.matcher(html);
		while (matcher.find()) {
			String s = matcher.group();
			s = s.substring(s.indexOf("src"));
			s = s.substring(s.indexOf(separator));
			list.add(s);
		}
		return list;
	}

	public static List<String> listRelativePathSrcUrl(String html, List<String> ignoreFolderList) {
		String imgTagRegex = "(src){1}\\s*=\\s*\"\\s*";
		String flag = "";
		if (ignoreFolderList != null && ignoreFolderList.size() > 0) {
			imgTagRegex += "(?!\\s*";
			for (String ignoreFolder : ignoreFolderList) {
				if (!ignoreFolder.startsWith(separator)) {
					ignoreFolder = separator + ignoreFolder;
				}
				if (!ignoreFolder.endsWith(separator)) {
					ignoreFolder += separator;
				}
				imgTagRegex += flag + ignoreFolder;
				flag = "|";
			}
			imgTagRegex += ")";
		}
		imgTagRegex += "[^\".]*\\.[a-zA-Z]+";
		Pattern imgTagPattern = Pattern.compile(imgTagRegex, Pattern.CASE_INSENSITIVE);
		List<String> list = new ArrayList<String>();
		Matcher matcher = imgTagPattern.matcher(html);
		while (matcher.find()) {
			String s = matcher.group();
			s = s.substring(s.indexOf("src"));
			s = s.substring(s.indexOf(separator));
			list.add(s);
		}
		return list;
	}

}
