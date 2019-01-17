package com.zlm.subtitlelibrary.entity;

/**
 * @Description: 字幕行数据
 * @author: zhangliangming
 * @date: 2019-01-12 15:55
 **/
public class SubtitleLineInfo {
    /**
     * 开始时间
     */
    private int startTime;
    /**
     * 结束时间
     */
    private int endTime;

    /**
     * 字幕内容
     */
    private String subtitleText;

    /**
     * 样式字幕内容
     */
    private String subtitleHtml;

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getSubtitleText() {
        return subtitleText;
    }

    public void setSubtitleText(String subtitleText) {
        this.subtitleText = subtitleText.replaceAll("\r", "");
    }

    public String getSubtitleHtml() {
        return subtitleHtml;
    }

    public void setSubtitleHtml(String subtitleHtml) {
        this.subtitleHtml = subtitleHtml.replaceAll("\r|\n", "");
    }
}
