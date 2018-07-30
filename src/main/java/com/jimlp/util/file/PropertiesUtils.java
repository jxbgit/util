package com.jimlp.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class PropertiesUtils {
    // 当前系统换行符
    private static final String SEPARATOR = System.getProperty("line.separator");

    /**
     * 修改或添加 properties 配置文件中的属性或注释。原内容格式保持不变<br>
     * 
     * @param file
     *            指定要修改的配置文件
     * @param params
     *            指定要修改的参数<br>
     *            例如：<br>
     *            #参数名=注释内容<br>
     *            参数名=参数值<br>
     *            将设置为：<br>
     *            #注释内容<br>
     *            参数名=参数值<br>
     * @return 始终返回 true
     * @throws IOException
     */
    public static boolean setValue(File file, LinkedHashMap<String, String> params) throws IOException {
        return setValue(file, params, false);
    }

    /**
     * 修改或添加 properties 配置文件中的属性或注释。原内容格式保持不变<br>
     * 若文件中不存在 params 中键值，则将在文件末尾追加此键值。
     * 
     * @param file
     *            指定要修改的配置文件
     * @param params
     *            指定要修改的参数<br>
     *            例如：<br>
     *            #参数名=注释内容<br>
     *            参数名=参数值<br>
     *            将设置为：<br>
     *            #注释内容<br>
     *            参数名=参数值<br>
     * @param append
     *            若原配置文件中不存在 params 中键值，是否将这些键值追加到配置文件末尾。
     * @return 始终返回 true
     * @throws IOException
     */
    public static boolean setValue(File file, LinkedHashMap<String, String> params, boolean append) throws IOException {
        @SuppressWarnings("unchecked")
        HashMap<String, String> _params = (HashMap<String, String>) params.clone();
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), FileUtils.getFileEncode(file)));// 数据流读取文件
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
                    strBuffer.append(SEPARATOR);
                    preNoteValTemp = null;
                }
                strBuffer.append(SEPARATOR);
                continue next;
            }
            char[] value = valTemp.toCharArray();
            for (int i = 0, l = value.length; i < l; i++) {
                if (keyStart == -1) {
                    if (value[i] == '#' || value[i] == '!') {
                        if (preNoteValTemp != null) {
                            strBuffer.append(preNoteValTemp);
                            strBuffer.append(SEPARATOR);
                            preNoteValTemp = valTemp;
                            continue next;
                        }
                        preNoteValTemp = valTemp;
                        note = true;
                        break;
                    }
                    if (value[i] != ' ' && value[i] != '\t' && value[i] != '\f') {
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
                    if (value[i] != ' ' && value[i] != '\t' && value[i] != '\f') {
                        valStart = i;
                    }
                } else if (valEnd == -1) {
                    if (i == l - 1 || value[i] == ' ' || value[i] != '\t') {
                        valEnd = i;
                    }
                }
            }
            if (!note && equalSign == -1) {
                strBuffer.append(valTemp);
                strBuffer.append(SEPARATOR);
                continue next;
            }
            if (valStart == -1) {
                valStart = valEnd = equalSign + 1;
            }
            if (!note) {
                key = valTemp.substring(keyStart, keyEnd + 1);
                noteKey = "#" + key;
                if (_params.containsKey(noteKey)) {
                    params.remove(noteKey);
                    preNoteValTemp = _params.get(noteKey);
                    preNoteValTemp = preNoteValTemp == null ? "" : preNoteValTemp;
                    preNoteValTemp = "#" + preNoteValTemp;
                } else {
                    noteKey = "!" + key;
                    if (_params.containsKey(noteKey)) {
                        params.remove(noteKey);
                        preNoteValTemp = _params.get(noteKey);
                        preNoteValTemp = preNoteValTemp == null ? "" : preNoteValTemp;
                        preNoteValTemp = "!" + preNoteValTemp;
                    }
                }
                if (preNoteValTemp != null) {
                    strBuffer.append(preNoteValTemp);
                    strBuffer.append(SEPARATOR);
                }
                if (_params.containsKey(key)) {
                    params.remove(key);
                    strBuffer.append(valTemp.substring(0, valStart));
                    valTemp = _params.get(key);
                    valTemp = valTemp == null ? "" : valTemp;
                }
                preNoteValTemp = null;
                strBuffer.append(valTemp);
                strBuffer.append(SEPARATOR);
            }
        }
        if (preNoteValTemp != null) {
            strBuffer.append(preNoteValTemp);
            strBuffer.append(SEPARATOR);
        }
        try {
            bufReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (append && params.size() > 0) {
            String _key = null;
            for (Entry<String, String> entry : params.entrySet()) {
                _key = entry.getKey();
                if (_key == null || _key.trim().length() == 0) {
                    continue;
                }
                if (!_key.startsWith("#") && !_key.startsWith("!")) {
                    if (params.containsKey("#" + _key) || params.containsKey("!" + _key)) {
                        strBuffer.append('#');
                        String noteVal = params.get("#" + _key);
                        noteVal = noteVal == null ? params.get("!" + _key) : noteVal;
                        strBuffer.append(noteVal == null ? "" : noteVal);
                        strBuffer.append(SEPARATOR);
                    }
                    strBuffer.append(_key);
                    strBuffer.append('=');
                    strBuffer.append(entry.getValue());
                    strBuffer.append(SEPARATOR);
                }
            }
        }
        PrintWriter printWriter = new PrintWriter(file, FileUtils.getFileEncode(file));
        printWriter.write(strBuffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
        return true;
    }
}
