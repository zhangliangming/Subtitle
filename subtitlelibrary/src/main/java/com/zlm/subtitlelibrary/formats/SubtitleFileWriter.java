package com.zlm.subtitlelibrary.formats;

import com.zlm.subtitlelibrary.entity.SubtitleInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * @Description: 字幕文件保存器
 * @author: zhangliangming
 * @date: 2019-01-12 15:54
 **/
public abstract class SubtitleFileWriter {
    /**
     * 默认编码
     */
    private Charset defaultCharset = Charset.forName("utf-8");

    /**
     * 保存文件
     *
     * @param subtitleInfo
     * @param filePath
     * @return
     * @throws Exception
     */
    public abstract boolean writer(SubtitleInfo subtitleInfo, String filePath)
            throws Exception;


    /**
     * 保存文件
     *
     * @param subtitleContent
     * @param filePath
     * @return
     * @throws Exception
     */
    public boolean saveFile(String subtitleContent, String filePath) throws Exception {

        File saveFile = new File(filePath);
        if (saveFile != null) {
            //
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            OutputStreamWriter outstream = new OutputStreamWriter(
                    new FileOutputStream(filePath),
                    getDefaultCharset());
            PrintWriter writer = new PrintWriter(outstream);
            writer.write(subtitleContent);
            writer.close();

            outstream = null;
            writer = null;

            return true;
        }

        return false;
    }


    /**
     * 保存文件
     *
     * @param subtitleContent
     * @param filePath
     * @return
     * @throws Exception
     */
    public boolean saveFile(byte[] subtitleContent, String filePath) throws Exception {

        File saveFile = new File(filePath);
        if (saveFile != null) {

            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            FileOutputStream os = new FileOutputStream(saveFile);
            os.write(subtitleContent);
            os.close();

            os = null;

            return true;
        }
        return false;
    }

    /**
     * 获取字幕保存内容
     *
     * @param subtitleInfo
     * @return
     * @throws Exception
     */
    public abstract String getSubtitleContent(SubtitleInfo subtitleInfo) throws Exception;


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
