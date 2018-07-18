package com.jimlp.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class PropertiesUtils {
    /**
     * 修改或添加 properties 文件中的属性。<br>
     * 若文件中存在两个相同的键值，则只对第一个做修改。<br>
     * 若文件中不存在 params 中键值，则将在文件末尾追加此键值。
     * 
     * @param file
     *            指定要修改的配置文件
     * @param params
     *            指定要修改的参数
     * @return 始终返回 true
     * @throws IOException
     */
    public static boolean setValue(File file, LinkedHashMap<String, String> params) throws IOException {
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));// 数据流读取文件
        StringBuffer strBuffer = new StringBuffer();
        String key = null;
        String noteKey = null;

        // 当前行或值
        String valTemp = null;
        int keyStart = -1;
        int keyEnd = -1;
        int valStart = -1;
        int valEnd = -1;
        int equalSign = -1;
        boolean note = false;
        // 前一行注释
        String preNoteValTemp = null;
        next: while ((valTemp = bufReader.readLine()) != null) {
            keyStart = -1;
            keyEnd = -1;
            valStart = -1;
            valEnd = -1;
            equalSign = -1;
            note = false;
            if (valTemp.trim().length() == 0) {
                if (preNoteValTemp != null) {
                    strBuffer.append(preNoteValTemp);
                    strBuffer.append(System.getProperty("line.separator"));
                    preNoteValTemp = null;
                }
                strBuffer.append(System.getProperty("line.separator"));
                continue next;
            }
            char[] value = valTemp.toCharArray();
            for (int i = 0, l = value.length; i < l; i++) {
                if (keyStart == -1 && value[i] == '#') {
                    if (preNoteValTemp != null) {
                        strBuffer.append(preNoteValTemp);
                        strBuffer.append(System.getProperty("line.separator"));
                        preNoteValTemp = valTemp;
                        continue next;
                    }
                    preNoteValTemp = valTemp;
                    note = true;
                    break;
                }
                if (keyStart == -1) {
                    if (value[i] != ' ' && value[i] != '\t') {
                        keyStart = i;
                    }
                } else if (keyEnd == -1) {
                    switch (value[i]) {
                    case '=':
                        equalSign = i;
                    case ' ':
                    case '\t':
                        if (keyEnd == -1) {
                            keyEnd = i - 1;
                        }
                        break;
                    }
                } else if (equalSign == -1) {
                    if ('=' == value[i]) {
                        equalSign = i;
                    }
                } else if (valStart == -1) {
                    if (value[i] != ' ' && value[i] != '\t') {
                        valStart = i;
                    }
                } else if (valEnd == -1) {
                    if (i == l - 1 || value[i] == ' ' || value[i] != '\t') {
                        valEnd = i;
                    }
                }
            }
            if (!note) {
                key = valTemp.substring(keyStart, keyEnd + 1);
                noteKey = "#" + key;
                if (preNoteValTemp != null) {
                    if (params.containsKey(noteKey)) {
                        preNoteValTemp = params.get(noteKey);
                        params.remove(noteKey);
                        strBuffer.append('#');
                    }
                    strBuffer.append(preNoteValTemp);
                    strBuffer.append(System.getProperty("line.separator"));
                }
                if (params.containsKey(key)) {
                    strBuffer.append(valTemp.substring(0, valStart));
                    valTemp = params.get(key);
                    params.remove(key);
                }
                preNoteValTemp = null;
                strBuffer.append(valTemp);
                strBuffer.append(System.getProperty("line.separator"));
            }
        }
        try {
            bufReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (params.size() > 0) {
            String _key = null;
            for (Entry<String, String> entry : params.entrySet()) {
                _key = entry.getKey();
                if (_key.startsWith("#")) {
                    strBuffer.append('#');
                } else {
                    strBuffer.append(_key);
                    strBuffer.append('=');
                }
                strBuffer.append(entry.getValue());
                strBuffer.append(System.getProperty("line.separator"));
            }
        }
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.write(strBuffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
        return true;
    }
}
