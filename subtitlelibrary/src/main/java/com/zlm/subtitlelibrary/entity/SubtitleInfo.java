package com.zlm.subtitlelibrary.entity;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @Description: 字幕集合
 * @author: zhangliangming
 * @date: 2019-01-12 15:56
 **/
public class SubtitleInfo {
    private String ext;
    private Charset defaultCharset;
    private List<SubtitleLineInfo> subtitleLineInfos;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Charset getDefaultCharset() {
        return defaultCharset;
    }

    public void setDefaultCharset(Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    public List<SubtitleLineInfo> getSubtitleLineInfos() {
        return subtitleLineInfos;
    }

    public void setSubtitleLineInfos(List<SubtitleLineInfo> subtitleLineInfos) {
        this.subtitleLineInfos = subtitleLineInfos;
    }

}
