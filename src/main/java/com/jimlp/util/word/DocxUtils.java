package com.jimlp.util.word;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.StringUtils;

import com.jimlp.util.ChineseUtils;
import com.jimlp.util.ProjectUtils;

import fr.opensagres.poi.xwpf.converter.core.BasicURIResolver;
import fr.opensagres.poi.xwpf.converter.core.FileImageExtractor;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;

/**
 * Word2007文档工具类。
 *
 * <br>
 * 创建时间 2017年12月22日下午1:39:38
 *
 * @author jxb
 *
 */
public final class DocxUtils {
    public static final String FILE_EXTENSIONS = ".docx";
    private static final char SLASH = '/';

    /**
     * Word2007 转 html 源码（针对 web 项目）。
     * 
     * @param word
     *            需要转换的Word文件（必须是 docx 格式的文件）。
     * @param baseFolder
     *            保存文件的文件夹路径（以“/”开头的相对于网站根目录的路径）。
     * 
     * @return 返回 html 源码。
     * 
     * @throws IOException
     */
    @Deprecated
    public static String parseHtmlSrc(File word, String baseFolder) throws IOException {
        return parseHtml(word, baseFolder, false, false);
    }

    /**
     * Word2007 转 html 文件（针对 web 项目）。
     * 
     * @param word
     *            需要转换的Word文件（必须是 docx 格式的文件）。
     * @param baseFolder
     *            保存文件的文件夹路径（以“/”开头的相对于网站根目录的路径）。
     * @param cover
     *            是否覆盖已有的 html 文件，默认覆盖。
     * 
     * @return 返回 html 文件用于web访问的 uri。
     * 
     * @throws IOException
     */
    @Deprecated
    public static String parseHtmlFile(File word, String baseFolder, boolean cover) throws IOException {
        return parseHtml(word, baseFolder, true, cover);
    }

    /**
     * Word2007 转 html（针对 web 项目）
     * 
     * @param word
     *            需要转换的Word文件（必须是 docx 格式的文件）。
     * @param baseFolder
     *            保存文件的文件夹路径（以“/”开头的相对于网站根目录的路径）。
     * @param makefile
     *            是否生成 html 文件（否则直接返回 html 源码）。
     * @param cover
     *            是否覆盖已有的 html 文件，默认覆盖（只有 makefile = true 时生效）。
     * 
     * @return 返回 html 源码或 html 文件用于web访问的 uri。
     * 
     * @throws IOException
     */
    @Deprecated
    public static String parseHtml(File word, String baseFolder, boolean makefile, boolean... cover) throws IOException {
        String wordName = word.getName();
        if (!StringUtils.endsWithIgnoreCase(wordName, FILE_EXTENSIONS)) {
            throw new IllegalArgumentException("无效的文件类型。");
        } else {
            wordName = wordName.substring(0, wordName.length() - 5);
        }

        // 1) 加载word文档生成 XWPFDocument对象
        InputStream in = new FileInputStream(word);
        XWPFDocument document = new XWPFDocument(in);

        // 路径处理
        String projectPath = ProjectUtils.getProjectPath();
        String imagesFolderName = "word-images/";
        baseFolder = baseFolder.replace('\\', SLASH);
        StringBuilder baseFolderSb = new StringBuilder(baseFolder);
        if (!baseFolder.startsWith(SLASH + "")) {
            baseFolderSb.insert(0, SLASH);
        }
        if (!baseFolder.endsWith(SLASH + "")) {
            baseFolderSb.append(SLASH);
        }
        baseFolderSb.append(ChineseUtils.toPinYinOfFirstUpperCase(wordName)).append(SLASH);

        // 2) 解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录)
        File imagesFolder = new File(new StringBuilder(projectPath).append(baseFolderSb).append(imagesFolderName).toString());

        XHTMLOptions options = XHTMLOptions.create();
        options.setExtractor(new FileImageExtractor(imagesFolder));
        // html中图片的路径 相对路径
        options.URIResolver(new BasicURIResolver(new StringBuilder(baseFolderSb).append(imagesFolderName).toString()));
        options.setIgnoreStylesIfUnused(false);
        options.setFragment(true);

        // 3) 将 XWPFDocument转换成XHTML
        // 使用字符数组流获取解析的内容
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XHTMLConverter.getInstance().convert(document, baos, options);

        // 输出 html 源码
        if (!makefile) {
            return baos.toString();
        }

        String htmlFolderName = "word-html/";
        // html文件相对项目根目录的路径
        StringBuilder returnPath = new StringBuilder(baseFolderSb).append(htmlFolderName).append(wordName).append(".html");
        // html文件保存路径
        StringBuilder htmlSavePath = new StringBuilder(projectPath).append(returnPath);

        // html文件
        File htmlFile = new File(htmlSavePath.toString());
        // 是否已存在 HTML 文件
        if (cover.length > 0 && !cover[0] && htmlFile.exists()) {
            return returnPath.toString();
        }
        // 生成html文件上级文件夹
        File htmlFileFolder = htmlFile.getParentFile();
        if (!htmlFileFolder.exists()) {
            htmlFileFolder.mkdirs();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(htmlFile);
            out.write(baos.toByteArray());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnPath.toString();
    }
}
