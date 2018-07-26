package com.jimlp.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

/**
 * <p>
 * 文件处理的相关工具类（所有方法的参数列表中表示路径的参数默认都为绝对路径）
 * 
 * @author JIML
 *
 */
public final class FileUtils {

	/**
	 * 以字符串形式输出文件内容
	 * 
	 * @param srcFileName
	 *            源文件
	 * @throws IOException
	 *             当文件长度大于 2147483647 字节时或者出现系统I/O异常
	 * 
	 * @return
	 */
	public static String readToString(String srcFileName) throws IOException {
		File f = new File(srcFileName);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			long l = f.length();
			if (l > Integer.MAX_VALUE) {
				throw new IOException("文件过大，超过" + Integer.MAX_VALUE + "字节");
			}
			byte[] bs = new byte[(int) f.length()];
			fis.read(bs);
			return new String(bs, getFileEncode(f));
		} catch (IOException e) {
			throw e;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}
	
	/**
	 * 复制文件（若目标位置已有同名文件，则覆盖）
	 * 
	 * @param srcFileName
	 *            源文件
	 * @param targetFileName
	 *            目标文件
	 * @throws IOException
	 */
	public static void copyFile(String srcFileName, String targetFileName) throws IOException {
		copyFile(new File(srcFileName), new File(targetFileName));
	}

	/**
	 * 复制文件（若目标位置已有同名文件，则覆盖）
	 * 
	 * @param srcFile
	 *            源文件
	 * @param targetFile
	 *            目标文件
	 * @throws IOException
	 */
	public static void copyFile(File srcFile, File targetFile) throws IOException {
		if (!targetFile.exists()) {
			File f = targetFile.getParentFile();
			if (!f.exists()) {
				f.mkdirs();
			}
			targetFile.createNewFile();
		}
		copyFile(new FileInputStream(srcFile), new FileOutputStream(targetFile));
	}

	/**
	 * 复制文件（若目标位置已有同名文件，则覆盖）
	 * 
	 * @param src
	 *            源文件
	 * @param target
	 *            目标文件
	 * @return
	 * @throws IOException
	 */
	public static long copyFile(FileInputStream src, FileOutputStream target) throws IOException {
		FileChannel in = null;
		FileChannel out = null;
		try {
			// 得到对应的文件通道
			in = src.getChannel();
			// 得到对应的文件通道
			out = target.getChannel();
			// 连接两个通道，并且从in通道读取，然后写入out通道
			return in.transferTo(0, in.size(), out);
		} finally {
			if (src != null) {
				try {
					src.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (target != null) {
				try {
					target.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * <p>
	 * 清空文件夹 或 删除文件
	 * 
	 * @param file
	 *            文件夹[文件]
	 * 
	 * @return 当且仅当目标文件夹[文件]不存在 或 清空[删除]成功后返回 true ，否则返回 false 。
	 */
	public static boolean emptyFile(File file) {
		// 此抽象路径名表示的文件或目录不存在
		if (!file.exists()) {
			return true;
		}
		// 此抽象路径名表示的是一个目录
		if (file.isDirectory()) {
			// 返回一个字符串数组，这些字符串指定此抽象路径名表示的目录中的文件和目录。
			File[] fileList = file.listFiles();
			for (int i = 0, l = fileList.length; i < l; i++) {
				File f = fileList[i];
				// 递归目录
				if (f.isDirectory()) {
					if (!emptyFile(f)) {
						return false;
					}
				}
				// 保证删除内部空文件夹
				if (!f.delete()) {
					return false;
				}
			}
			return true;
			// 此抽象路径名表示的是一个文件
		} else {
			return file.delete();
		}
	}

	/**
	 * <p>
	 * 删除 文件夹[文件]
	 * 
	 * @param path
	 *            文件夹[文件]路径
	 * 
	 * @return 当且仅当目标文件夹[文件]不存在 或 删除成功返回 true ，否则返回 false 。
	 */
	public static boolean delete(File file) {
		if (!file.exists()) {
			return true;
		}
		if (file.isDirectory()) {
			if (!emptyFile(file)) {
				return false;
			}
		}
		// 删除文件或最后剩下的空目录
		return file.delete();
	}

	/**
	 * <p>
	 * 删除 文件夹[文件]（成功删除目标文件夹[文件]后会尝试向上逐层删除与之有直接联系的空文件夹，不保证上级空文件夹一定被删除。）
	 * 
	 * @param path
	 *            文件夹[文件]路径
	 * 
	 * @return 当且仅当目标文件夹[文件]不存在 或 删除成功返回 true ，否则返回 false 。
	 */
	public static boolean upDelete(String filePath) {
		File file = new File(filePath);
		// 目标目录或文件存在
		if (file.exists()) {
			// 删除失败
			if (!file.delete()) {
				return false;
			}
			boolean flag = true;
			// 尝试向上层删除
			while (flag) {
				file = file.getParentFile();
				flag = file.delete();
			}
		}
		return true;
	}
	
	/**
     * 获取文件编码格式
     * 
     * @param path
     *            要判断文件编码格式的源文件的路径
     * @return 返回文件编码格式
     */
    public static String getFileEncode(String path) {
        File file = new File(path);
        return getFileEncode(file);
    }
	
    /**
     * 获取文件编码格式
     * 
     * @param file
     *            要判断文件编码格式的源文件
     * @return 返回文件编码格式
     */
    public static String getFileEncode(File file) {
        /*
         * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
         * JChardetFacade、ASCIIDetector、UnicodeDetector。
         * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
         * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
         * cpDetector是基于统计学原理的，不保证完全正确。
         */
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        /*
         * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
         * 指示是否显示探测过程的详细信息，为false不显示。
         */
        detector.add(new ParsingDetector(false));
        /*
         * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
         * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
         * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
         */
        detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar
        // ASCIIDetector用于ASCII编码测定
        detector.add(ASCIIDetector.getInstance());
        // UnicodeDetector用于Unicode家族编码的测定
        detector.add(UnicodeDetector.getInstance());
        Charset charset = null;
        try {
            charset = detector.detectCodepage(file.toURI().toURL());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (charset != null)
            return charset.name();
        else
            return null;
    }
}
