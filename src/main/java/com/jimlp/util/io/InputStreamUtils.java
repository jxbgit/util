package com.jimlp.util.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 *
 * <br>
 * 创建时间 2018年6月21日上午11:08:44
 *
 * @author jxb
 *
 */
public class InputStreamUtils {
    /**
     * 将输入流转字节数组
     * 
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        BufferedInputStream bin = null;
        ByteArrayOutputStream out = null;
        try {
            bin = new BufferedInputStream(inputStream);
            out = new ByteArrayOutputStream();
            int size = 0;
            byte[] buf = new byte[1024];
            while ((size = bin.read(buf)) != -1) {
                out.write(buf, 0, size);
            }
            return out.toByteArray();
        } finally {
            if (bin != null) {
                bin.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 将输入流转字符串
     * 
     * @param stream
     * @param charset
     * @return
     * @throws IOException
     */
    public static String inputStreamToString(InputStream stream, String charset) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }

            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * 将输入流转字符串
     * 
     * @param reader
     * @return
     * @throws IOException
     */
    public static String inputStreamToString(Reader reader) throws IOException {
        try {
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }

            return response.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
