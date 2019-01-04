package com.jimlp.util.zip;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.jimlp.util.StringUtils;
import com.jimlp.util.time.SimpleDateFormatUtils;

/**
 *
 * <br>
 * 创建时间 2018/12/18 13:40:33
 *
 * @author jxb
 *
 */
public class ZipUtils {
    /**
     * 压缩文件
     * 
     * @param sourceFile
     *            要压缩的文件或目录
     * @param out
     *            指定压缩文件输出流
     * @return 返回压缩包大小，略微小于实际大小
     * @throws IOException
     */
    public static long zip(File sourceFile, OutputStream out) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            return zip(sourceFile, zos, null, true, ZipEntry.DEFLATED, null);
        }
    }

    /**
     * 压缩文件
     * 
     * @param sourceFile
     *            要压缩的文件或目录
     * @param out
     *            指定压缩文件输出流
     * @param method
     *            压缩方法，可以为 {@link ZipEntry.STORED} 或 {@link ZipEntry.DEFLATED}
     * @return 返回压缩包大小，略微小于实际大小
     * @throws IOException
     */
    public static long zip(File sourceFile, OutputStream out, int method) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            return zip(sourceFile, zos, null, true, method, null);
        }
    }

    /**
     * 压缩文件
     * 
     * @param sourceFile
     *            要压缩的文件或目录
     * @param out
     *            指定压缩文件输出流
     * @param deep
     *            如果压缩一个目录，是否包含并压缩其子目录
     * @return 返回压缩包大小，略微小于实际大小
     * @throws IOException
     */
    public static long zip(File sourceFile, OutputStream out, boolean deep) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            return zip(sourceFile, zos, null, deep, ZipEntry.DEFLATED, null);
        }
    }

    /**
     * 压缩文件
     * 
     * @param sourceFile
     *            要压缩的文件或目录
     * @param out
     *            指定压缩文件输出流
     * @param deep
     *            如果压缩一个目录，是否包含并压缩其子目录
     * @param method
     *            压缩方法，可以为 {@link ZipEntry.STORED} 或 {@link ZipEntry.DEFLATED}
     * @return 返回压缩包大小，略微小于实际大小
     * @throws IOException
     */
    public static long zip(File sourceFile, OutputStream out, boolean deep, int method) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            return zip(sourceFile, zos, null, deep, method, null);
        }
    }

    /**
     * 压缩文件
     * 
     * @param sourceFile
     *            要压缩的文件或目录
     * @param out
     *            指定压缩文件输出流
     * @param deep
     *            如果压缩一个目录，是否包含并压缩其子目录
     * @param method
     *            压缩方法，可以为 {@link ZipEntry.STORED} 或 {@link ZipEntry.DEFLATED}
     * @param zipRootDir
     *            文件压缩到指定的根目录（a、a/b、a/b/c）
     * @return 返回压缩包大小，略微小于实际大小
     * @throws IOException
     */
    public static long zip(File sourceFile, OutputStream out, boolean deep, int method, String zipRootDir) throws IOException {
        return zip(sourceFile, out, deep, method, zipRootDir, null);
    }

    /**
     * 压缩文件
     * 
     * @param sourceFile
     *            要压缩的文件或目录
     * @param out
     *            指定压缩文件输出流
     * @param deep
     *            如果压缩一个目录，是否包含并压缩其子目录
     * @param method
     *            压缩方法，可以为 {@link ZipEntry.STORED} 或 {@link ZipEntry.DEFLATED}
     * @param fileFilter
     *            文件过滤器
     * @return 返回压缩包大小，略微小于实际大小
     * @throws IOException
     */
    public static long zip(File sourceFile, OutputStream out, boolean deep, int method, FileFilter fileFilter) throws IOException {
        return zip(sourceFile, out, deep, method, null, fileFilter);
    }

    /**
     * 压缩文件
     * 
     * @param sourceFile
     *            要压缩的文件或目录
     * @param out
     *            指定压缩文件输出流
     * @param deep
     *            如果压缩一个目录，是否包含并压缩其子目录
     * @param method
     *            压缩方法，可以为 {@link ZipEntry.STORED} 或 {@link ZipEntry.DEFLATED}
     * @param zipRootDir
     *            文件压缩到指定的根目录（a、a/b、a/b/c）
     * @param fileFilter
     *            文件过滤器
     * @return 返回压缩包大小，略微小于实际大小
     * @throws IOException
     */
    public static long zip(File sourceFile, OutputStream out, boolean deep, int method, String zipRootDir, FileFilter fileFilter) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            return zip(sourceFile, zos, zipRootDir, deep, method, fileFilter);
        }
    }

    /**
     * 压缩文件
     * 
     * @param sourceFile
     *            要压缩的文件或目录
     * @param zipFile
     *            指定压缩文件路径
     * @return 返回压缩包大小，略微小于实际大小
     * @throws IOException
     */
    public static long zip(File sourceFile, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile), StandardCharsets.UTF_8)) {
            return zip(sourceFile, zos, null, true, ZipEntry.DEFLATED, null);
        }
    }

    /**
     * 压缩文件
     * 
     * @param sourceFile
     *            要压缩的文件或目录
     * @param zipFile
     *            指定压缩文件路径
     * @param method
     *            压缩方法，可以为 {@link ZipEntry.STORED} 或 {@link ZipEntry.DEFLATED}
     * @return 返回压缩包大小，略微小于实际大小
     * @throws IOException
     */
    public static long zip(File sourceFile, File zipFile, int method) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile), StandardCharsets.UTF_8)) {
            return zip(sourceFile, zos, null, true, method, null);
        }
    }

    /**
     * 解压
     * 
     * @param is
     *            压缩文件输入流
     * @param outPath
     *            指定解压路径
     * @throws IOException
     */
    public static void unzip(InputStream is, String outPath) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(is, StandardCharsets.UTF_8)) {
            _unzip(zis, outPath);
        }
    }

    /**
     * 递归压缩方法
     * 
     * @param sourceFile
     *            源文件
     * @param zos
     *            zip输出流
     * @param zipRootDir
     *            文件压缩到指定的根目录（a、a/b、a/b/c）
     * @param deep
     *            如果压缩一个目录，是否包含并压缩其子目录
     * @param method
     *            压缩方法，可以为 {@link ZipEntry.STORED} 或 {@link ZipEntry.DEFLATED}
     * @param fileFilter
     *            文件过滤器
     * @return 返回压缩包大小，略微小于实际大小
     * @throws Exception
     */
    public static long zip(File sourceFile, ZipOutputStream zos, String zipRootDir, boolean deep, int method, FileFilter fileFilter) throws IOException {
        long length = 0;
        zipRootDir = StringUtils.isEmpty(zipRootDir) ? "" : zipRootDir;
        if (!sourceFile.isDirectory() && (fileFilter == null || fileFilter.accept(sourceFile))) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            ZipEntry ze = new ZipEntry(zipRootDir);
            if (method == ZipEntry.STORED) {
                ze.setMethod(method);
                ze.setSize(sourceFile.length());
                ze.setCrc(calFileCRC32(sourceFile));
            }
            zos.putNextEntry(ze);
            // copy文件到zip输出流中
            try (FileInputStream in = new FileInputStream(sourceFile)) {
                int len;
                byte[] buf = new byte[2048];
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
            }
            // Complete the entry
            zos.closeEntry();
            length += ze.getCompressedSize();
        } else {
            File[] listFiles = sourceFile.listFiles(fileFilter);
            if (listFiles == null || listFiles.length == 0) {
                // 空文件夹的处理
                ZipEntry ze = new ZipEntry(zipRootDir + "/");
                zos.putNextEntry(ze);
                // 没有文件，不需要文件的copy
                zos.closeEntry();
                length += ze.getCompressedSize();
            } else {
                for (File file : listFiles) {
                    if (!deep && file.isDirectory()) {
                        continue;
                    }
                    // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                    // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                    length += zip(file, zos, zipRootDir.length() != 0 ? zipRootDir + "/" + file.getName() : file.getName(), deep, method, fileFilter);
                }
            }
        }
        return length;
    }

    private static void _unzip(ZipInputStream zis, String basePath) throws IOException {
        ZipEntry entry = zis.getNextEntry();
        if (entry != null) {
            File file = new File(basePath + File.separator + entry.getName());
            if (entry.isDirectory()) {
                // 可能存在空文件夹
                if (!file.exists()) {
                    file.mkdirs();
                } else {
                    if (!file.isDirectory()) {
                        file.renameTo(new File(file.getParent() + File.separator + file.getName() + "_bak" + SimpleDateFormatUtils.format(new Date(), "yyyyMMddHHmmss")));
                        file.mkdirs();
                    }
                }
                _unzip(zis, basePath);
            } else {
                // 输出流创建文件时必须保证父路径存在
                File parentFile = file.getParentFile();
                if (parentFile != null) {
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    } else {
                        if (!parentFile.isDirectory()) {
                            parentFile.renameTo(new File(parentFile.getParent() + File.separator + parentFile.getName() + "_bak" + SimpleDateFormatUtils.format(new Date(), "yyyyMMddHHmmss")));
                            parentFile.mkdirs();
                        }
                    }
                }
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    int len = 0;
                    byte[] buf = new byte[2048];
                    while ((len = zis.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                }
                zis.closeEntry();
                _unzip(zis, basePath);
            }
        }
    }

    public static long calFileCRC32(File file) throws IOException {
        FileInputStream fi = new FileInputStream(file);
        CheckedInputStream checksum = new CheckedInputStream(fi, new CRC32());
        byte[] buf = new byte[2048];
        while (checksum.read(buf) != -1) {
        }
        long temp = checksum.getChecksum().getValue();
        fi.close();
        checksum.close();
        return temp;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        zip(new File("C:/Users/LY/Desktop/other"), new File("C:/Users/LY/Desktop/ot.zip"));
    }
}
