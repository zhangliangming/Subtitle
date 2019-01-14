package com.zlm.subtitlelibrary;

import com.zlm.subtitlelibrary.entity.SubtitleInfo;
import com.zlm.subtitlelibrary.formats.SubtitleFileReader;
import com.zlm.subtitlelibrary.util.SubtitleUtil;

import java.io.File;

/**
 * @Description: 字体读取类
 * @author: zhangliangming
 * @date: 2019-01-13 16:44
 **/
public class SubtitleReader {
    /**
     * 时间补偿值,其单位是毫秒，正值表示整体提前，负值相反。这是用于总体调整显示快慢的。
     */
    private long defOffset = 0;
    /**
     * 增量
     */
    private long offset = 0;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件hash
     */
    private String hash;

    /**
     * 字幕数据集合
     */
    private SubtitleInfo subtitleInfo;

    public SubtitleReader() {

    }

    /**
     * @throws
     * @Description: 读取字幕文件
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2019-01-12 19:12
     */
    public void readFile(File file) throws Exception {
        if (file != null) {
            filePath = file.getPath();
            SubtitleFileReader subtitleFileReader = SubtitleUtil.getSubtitleFileReader(file);
            subtitleInfo = subtitleFileReader.readFile(file);
        }
    }

    /**
     * 读取字幕内容并保存到文件
     *
     * @param fileContentString
     * @param saveFile          不能为空
     * @throws Exception
     */
    public void readText(String fileContentString, File saveFile) throws Exception {
        if (saveFile != null) {
            filePath = saveFile.getPath();
            SubtitleFileReader subtitleFileReader = SubtitleUtil.getSubtitleFileReader(saveFile);
            subtitleInfo = subtitleFileReader.readText(fileContentString, saveFile);
        }
    }

    /**
     * 播放的时间补偿值
     *
     * @return
     */
    public long getPlayOffset() {
        return defOffset + offset;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public SubtitleInfo getSubtitleInfo() {
        return subtitleInfo;
    }

    public void setSubtitleInfo(SubtitleInfo subtitleInfo) {
        this.subtitleInfo = subtitleInfo;
    }

}
