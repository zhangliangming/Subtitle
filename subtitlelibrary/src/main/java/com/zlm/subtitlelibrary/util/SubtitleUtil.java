package com.zlm.subtitlelibrary.util;

import android.text.TextUtils;

import com.zlm.subtitlelibrary.entity.SubtitleLineInfo;
import com.zlm.subtitlelibrary.formats.SubtitleFileReader;
import com.zlm.subtitlelibrary.formats.SubtitleFileWriter;
import com.zlm.subtitlelibrary.formats.ass.AssSubtitleFileReader;
import com.zlm.subtitlelibrary.formats.srt.SrtSubtitleFileReader;
import com.zlm.subtitlelibrary.formats.srt.SrtSubtitleFileWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 字幕处理工具
 * @author: zhangliangming
 * @date: 2019-01-12 15:57
 **/
public class SubtitleUtil {

    private static ArrayList<SubtitleFileReader> readers;
    private static ArrayList<SubtitleFileWriter> writers;

    static {
        readers = new ArrayList<SubtitleFileReader>();
        readers.add(new SrtSubtitleFileReader());
        readers.add(new AssSubtitleFileReader());
        //
        writers = new ArrayList<SubtitleFileWriter>();
        writers.add(new SrtSubtitleFileWriter());
    }

    /**
     * 获取支持的文件格式
     *
     * @return
     */
    public static List<String> getSupportSubtitleExts() {
        List<String> lrcExts = new ArrayList<String>();
        for (SubtitleFileReader subtitleFileReader : readers) {
            lrcExts.add(subtitleFileReader.getSupportFileExt());
        }
        return lrcExts;
    }

    /**
     * 获取文件读取器
     *
     * @param file
     * @return
     */
    public static SubtitleFileReader getSubtitleFileReader(File file) {
        return getSubtitleFileReader(file.getName());
    }

    /**
     * 获取歌词文件读取器
     *
     * @param fileName
     * @return
     */
    public static SubtitleFileReader getSubtitleFileReader(String fileName) {
        String ext = FileUtil.getFileExt(fileName);
        for (SubtitleFileReader subtitleFileReader : readers) {
            if (subtitleFileReader.isFileSupported(ext)) {
                return subtitleFileReader;
            }
        }
        return null;
    }

    /**
     * 获取保存器
     *
     * @param file
     * @return
     */
    public static SubtitleFileWriter getSubtitleFileWriter(File file) {
        return getSubtitleFileWriter(file.getName());
    }

    /**
     * 获取保存器
     *
     * @param fileName
     * @return
     */
    public static SubtitleFileWriter getSubtitleFileWriter(String fileName) {
        String ext = FileUtil.getFileExt(fileName);
        for (SubtitleFileWriter subtitleFileWriter : writers) {
            if (subtitleFileWriter.isFileSupported(ext)) {
                return subtitleFileWriter;
            }
        }
        return null;
    }

    /**
     * 解析字幕文本
     *
     * @param subtitleLine
     * @return html格式对应的字幕文本
     */
    public static String[] parseSubtitleText(String subtitleLine) {
        String[] result = {"", ""};
        String regex = "\\{[^\\{]+\\}";
        //去掉样式
        result[0] = subtitleLine.replaceAll(regex, "");
        //加载样式
        Pattern tempPattern = Pattern.compile(regex);
        Matcher tempMatcher = tempPattern.matcher(subtitleLine);
        if (tempMatcher.find()) {
            StringBuilder subtitleTextSB = new StringBuilder();
            String[] splitSubtitles = subtitleLine.split(regex, -1);
            int index = 0;

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(subtitleLine);
            //遍历样式字符串
            while (matcher.find()) {
                if (index == 0 && splitSubtitles.length > 0 && !TextUtils.isEmpty(splitSubtitles[0])) {
                    subtitleTextSB.append(splitSubtitles[0]);
                }
                String styleString = matcher.group();
                if (index + 1 >= splitSubtitles.length) {
                    break;
                }
                String splitSubtitle = splitSubtitles[index + 1];
                String subtitleText = getSubtitleText(styleString, splitSubtitle);
                subtitleTextSB.append(subtitleText);

                index++;
            }

            //如果没有样式
            if (index == 0 && splitSubtitles.length > 0 && !TextUtils.isEmpty(splitSubtitles[0])) {
                subtitleTextSB.append(splitSubtitles[0]);
            }
            //添加剩余的字幕内容
            for (index++; index < splitSubtitles.length; index++) {
                if (!TextUtils.isEmpty(splitSubtitles[index])) {
                    subtitleTextSB.append(splitSubtitles[index]);
                }
            }
            result[1] = subtitleTextSB.toString();
        } else {
            result[1] = subtitleLine;
        }
        return result;
    }

    /**
     * 获取字幕文本
     *
     * @param styleString   样式字符串
     * @param splitSubtitle 分隔后的字幕文本
     * @return
     */
    private static String getSubtitleText(String styleString, String splitSubtitle) {
        StringBuilder result = new StringBuilder();
        int start = styleString.indexOf("{");
        int end = styleString.lastIndexOf("}");
        styleString = styleString.substring(start + 1, end);
        styleString = styleString.replaceAll("\\\\", "\\$");
        if (styleString.contains("$")) {
            result.append("<font");
            String[] styles = styleString.split("\\$");
            for (int i = 0; i < styles.length; i++) {
                String style = styles[i];
                if (style.startsWith("fn")) {
                    String face = style.substring("fn".length()).trim();
                    result.append(" face=\"" + face + "\"");

                } else if (style.startsWith("fs")) {
                    String size = style.substring("fs".length()).trim();
                    result.append(" size=\"" + size + "\"");

                } else if (style.startsWith("b1") || style.startsWith("i1") || style.startsWith("u1") || style.startsWith("s1")) {
                    //b<0/1>  粗体，i<0/1>   斜体，u<0/1> 下划线，s<0/1>  删除线（0=关闭，1=开启）

                    if (style.startsWith("b1")) {
                        splitSubtitle = "<b>" + splitSubtitle + "</b>";
                    } else if (style.startsWith("i1")) {
                        splitSubtitle = "<i>" + splitSubtitle + "</i>";
                    } else if (style.startsWith("u1")) {
                        splitSubtitle = "<u>" + splitSubtitle + "</u>";
                    } else if (style.startsWith("s1")) {
                        splitSubtitle = "<s>" + splitSubtitle + "</s>";
                    }

                } else if (style.startsWith("c&H") || style.startsWith("1c&H")) {
                    //c&H<bbggrr>&     改变主体颜色（同1c）
                    //1c&H<bbggrr>&   改变主体颜色
                    int endIndex = style.lastIndexOf("&");
                    style = style.substring(0, endIndex).trim();
                    String color = "";
                    if (style.startsWith("c&H")) {
                        color = convertRgbColor(style.substring("c&H".length()).trim());
                    } else {
                        color = convertRgbColor(style.substring("1c&H".length()).trim());
                    }
                    result.append(" color=\"#" + color + "\"");

                }
            }
            result.append(">");
        }
        //修改成html标签
        if (result.length() > 0) {
            result.append(splitSubtitle);
            result.append("</font>");
        } else {
            result.append(splitSubtitle);
        }
        return result.toString();
    }

    /**
     * 获取rgb颜色字符串
     * 版权归作者所有，任何形式转载请联系作者。
     * 作者：无条件积极关注（来自豆瓣）
     * 来源：https://www.douban.com/note/658520175/
     * <p>
     * 颜色格式：&Haabbggrr，均为十六进制，取值0-F。
     * 前2位(alpha)为透明度，00=不透明，FF=DEC255=全透明；后6是BGR蓝绿红颜色。 排在最前的00可以忽略不写, 如：{\c&HFF&}={\c&H0000FF&}为纯红色、&HFFFFFF=纯白色、&HC8000000=透明度为200的黑色。
     *
     * @param abgrColorString
     * @return
     */
    public static String convertArgbColor(String abgrColorString) {
        if (abgrColorString.length() == 8) {
            return abgrColorString.substring(6, 8) + abgrColorString.substring(4, 6) + abgrColorString.substring(2, 4);
        }
        return abgrColorString.substring(4, 6) + abgrColorString.substring(2, 4) + abgrColorString.substring(0, 2);
    }

    /**
     * 获取rgb颜色字符串
     *
     * @param bgrColorString
     * @return
     */
    public static String convertRgbColor(String bgrColorString) {
        return convertArgbColor(bgrColorString);
    }

    /**
     * 获取bgr颜色字符串
     *
     * @param rgbColorString
     * @return
     */
    public static String convertBgrColor(String rgbColorString) {
        return convertRgbColor(rgbColorString);
    }

    /**
     * 获取abgr颜色字符串
     *
     * @param argbColorString
     * @return
     */
    public static String convertAbgrColor(String argbColorString) {
        return convertRgbColor(argbColorString);
    }

    /**
     * 根据当前播放进度获取当前行字幕内容
     *
     * @param subtitleLineInfos
     * @param curPlayingTime
     * @param playOffset
     * @return
     */
    public static int getLineNumber(List<SubtitleLineInfo> subtitleLineInfos, long curPlayingTime, long playOffset) {
        if (subtitleLineInfos != null && subtitleLineInfos.size() > 0) {
            //添加歌词增量
            long nowPlayingTime = curPlayingTime + playOffset;
            for (int i = 0; i < subtitleLineInfos.size(); i++) {
                SubtitleLineInfo subtitleLineInfo = subtitleLineInfos.get(i);
                int lineStartTime = subtitleLineInfo.getStartTime();
                int lineEndTime = subtitleLineInfo.getEndTime();
                if (nowPlayingTime < lineStartTime) {
                    return -1;
                } else if (nowPlayingTime >= lineStartTime && nowPlayingTime <= lineEndTime) {
                    return i;
                }
            }
        }
        return -1;
    }
}
