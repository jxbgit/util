package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 *
 * <br>
 * 创建时间 2018年12月27日下午3:51:24
 *
 * @author jxb
 *
 */
public class FileContrast {
    public static void main(String[] args) throws Exception {
        String fp1 = "C:/Users/LY/Desktop/SQL_20181228.log";
        String fp2 = "C:/Users/LY/Desktop/SQL_20181228s.log";
        File f1 = new File(fp1);
        File f2 = new File(fp2);
        LineNumberReader l1 = new LineNumberReader(new InputStreamReader(new FileInputStream(f1),"UTF-8"));
        LineNumberReader l2 = new LineNumberReader(new InputStreamReader(new FileInputStream(f2),"GB2312"));
        String line = null;
        String line2 = null;
        String pLine = null;
        while ((line = l1.readLine()) != null) {
            line2 =  l2.readLine();
            if (!line.equals(line2)) {
                System.out.println(l1.getLineNumber() - 1 + " " + pLine);
                System.out.println(l1.getLineNumber() + " " + line);
                System.out.println(l1.getLineNumber() + " " + line2);
            } else {
                pLine = line;
            }
        }
    }
}
