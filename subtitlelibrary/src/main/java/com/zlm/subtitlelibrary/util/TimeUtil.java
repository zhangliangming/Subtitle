package com.zlm.subtitlelibrary.util;

/**
 * @Description: 时间处理类
 * @author: zhangliangming
 * @date: 2019-01-12 21:37
 **/
public class TimeUtil {
    /**
     * 解析字幕时间
     *
     * @param timeString 00:00:00,000
     * @return
     */
    public static int parseSubtitleTime(String timeString) {
        timeString = timeString.replace(",", ":");
        timeString = timeString.replace(".", ":");
        String timedata[] = timeString.split(":");
        int second = 1000;
        int minute = 60 * second;
        int hour = 60 * minute;
        int msec = 0;
        if (timedata[3].length() == 2) {
            msec = Integer.parseInt(timedata[3]) * 10;
        } else {
            msec = Integer.parseInt(timedata[3]);
        }
        return Integer.parseInt(timedata[0]) * hour + Integer.parseInt(timedata[1]) * minute
                + Integer.parseInt(timedata[2]) * second + msec;
    }

    /**
     * 毫秒转时间字符串
     *
     * @param msecTotal
     * @return 00:00:00,000
     */
    public static String parseHHMMSSFFFString(int msecTotal) {
        int msec = msecTotal % 1000;
        msecTotal /= 1000;
        int minute = msecTotal / 60;
        int hour = minute / 60;
        int second = msecTotal % 60;
        minute %= 60;
        return String.format("%02d:%02d:%02d,%03d", hour, minute, second, msec);
    }

    /**
     * 毫秒转时间字符串
     *
     * @param msecTotal
     * @return 00:00:00.00
     */
    public static String parseHHMMSSFFString(int msecTotal) {
        int msec = msecTotal % 1000;
        msecTotal /= 1000;
        int minute = msecTotal / 60;
        int hour = minute / 60;
        int second = msecTotal % 60;
        minute %= 60;
        return String.format("%02d:%02d:%02d.%02d", hour, minute, second, msec / 10);
    }

    /**
     * 毫秒转时间字符串
     *
     * @param msecTotal
     * @return 00:00:00
     */
    public static String parseHHMMSSString(int msecTotal) {
        msecTotal /= 1000;
        int minute = msecTotal / 60;
        int hour = minute / 60;
        int second = msecTotal % 60;
        minute %= 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
