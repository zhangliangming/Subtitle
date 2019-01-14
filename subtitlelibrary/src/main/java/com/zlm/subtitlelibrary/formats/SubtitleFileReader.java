package com.zlm.subtitlelibrary.formats;

import com.zlm.subtitlelibrary.entity.SubtitleInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * @Description: 字幕文件读取器
 * @author: zhangliangming
 * @date: 2019-01-12 15:53
 **/
public abstract class SubtitleFileReader {

    /**
     * 默认编码
     */
    private Charset defaultCharset = Charset.forName("utf-8");

    /**
     * @throws
     * @Description: 读取字幕文件
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2019-01-12 19:12
     */
    public SubtitleInfo readFile(File file) throws Exception {
        if (file != null) {
            return readInputStream(new FileInputStream(file));
        }
        return null;
    }

    /**
     * @throws
     * @Description: 读取文件流
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2019-01-12 19:28
     */
    public abstract SubtitleInfo readInputStream(InputStream in) throws Exception;

    /**
     * @throws
     * @Description: 读取字幕文本
     * @param: saveFile 需要保存的字幕文件对象
     * @return:
     * @author: zhangliangming
     * @date: 2019-01-12 19:29
     */
    public abstract SubtitleInfo readText(String fileContentString, File saveFile) throws Exception;


    /**
     * @throws
     * @Description: 保存文件
     * @param:saveFile 需要保存的字幕文件对象
     * @return:
     * @author: zhangliangming
     * @date: 2019-01-12 19:15
     *//**/
    public boolean saveFile(File saveFile, String fileContentString) throws Exception {
        if (saveFile != null) {

            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }


            OutputStreamWriter outstream = new OutputStreamWriter(
                    new FileOutputStream(saveFile),
                    getDefaultCharset());
            PrintWriter writer = new PrintWriter(outstream);
            writer.write(fileContentString);
            writer.close();

            outstream = null;
            writer = null;

            return true;
        }

        return false;
    }

    /**
     * 支持文件格式
     *
     * @param ext 文件后缀名
     * @return
     */
    public abstract boolean isFileSupported(String ext);

    /**
     * 获取支持的文件后缀名
     *
     * @return
     */
    public abstract String getSupportFileExt();

    public void setDefaultCharset(Charset charset) {
        defaultCharset = charset;
    }

    public Charset getDefaultCharset() {
        return defaultCharset;
    }
}
