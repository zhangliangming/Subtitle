package com.zlm.subtitlelibrary.util;

/**
 * @Description: 文件处理类
 * @author: zhangliangming
 * @date: 2019-01-13 16:19
 **/
public class FileUtil {

    public static String getFileExt(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos == -1)
            return "";
        return fileName.substring(pos + 1).toLowerCase();
    }


}
