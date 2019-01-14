package com.zlm.subtitlelibrary.formats.srt;

import android.text.TextUtils;

import com.zlm.subtitlelibrary.entity.SubtitleInfo;
import com.zlm.subtitlelibrary.entity.SubtitleLineInfo;
import com.zlm.subtitlelibrary.formats.SubtitleFileWriter;
import com.zlm.subtitlelibrary.util.SubtitleUtil;
import com.zlm.subtitlelibrary.util.TimeUtil;

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: src字幕保存器
 * @author: zhangliangming
 * @date: 2019-01-13 16:27
 **/
public class SrtSubtitleFileWriter extends SubtitleFileWriter {

    public SrtSubtitleFileWriter() {
        setDefaultCharset(Charset.forName("UTF-16"));
    }

    @Override
    public boolean writer(SubtitleInfo subtitleInfo, String filePath) throws Exception {
        String subtitleContent = getSubtitleContent(subtitleInfo);
        return saveFile(subtitleContent, filePath);
    }

    @Override
    public String getSubtitleContent(SubtitleInfo subtitleInfo) throws Exception {
        StringBuilder result = new StringBuilder();
        List<SubtitleLineInfo> subtitleLineInfos = subtitleInfo.getSubtitleLineInfos();
        if (subtitleLineInfos != null && subtitleLineInfos.size() > 0) {
            for (int i = 0; i < subtitleLineInfos.size(); i++) {
                SubtitleLineInfo subtitleLineInfo = subtitleLineInfos.get(i);
                result.append((i + 1) + "\n");
                result.append(TimeUtil.parseHHMMSSFFFString(subtitleLineInfo.getStartTime()) + " --> " + TimeUtil.parseHHMMSSFFFString(subtitleLineInfo.getEndTime()) + "\n");
                String lineText = getSubtitleLineText(subtitleLineInfo.getSubtitleHtml());
                result.append(lineText + "\n\n");
            }
        }
        return result.toString();
    }

    /**
     * 获取字幕行内容
     *
     * @param subtitleText
     * @return
     */
    private String getSubtitleLineText(String subtitleText) {
        StringBuilder result = new StringBuilder();
        String regex = "(\\<font[^\\<]+\\>)(\\<[bius]\\>)*[^\\<]+(\\</[bius]\\>)*(\\</font\\>)";
        int index = 0;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(subtitleText);
        String[] splitSubtitles = subtitleText.split(regex);
        while (matcher.find()) {
            if (index < splitSubtitles.length && !TextUtils.isEmpty(splitSubtitles[index])) {
                result.append(splitSubtitles[index]);
            }
            String styleString = matcher.group();
            String subtitleString = parseStyleString(styleString);
            result.append(subtitleString);
            index++;
        }
        if (index == 0) {
            result.append(subtitleText);
        }

        //添加剩余的字幕内容
        for (index++; index < splitSubtitles.length; index++) {
            result.append(splitSubtitles[index]);
        }

        return result.toString().replaceAll("<br>", "\n");
    }

    /**
     * 解析style对应的字符内容
     *
     * @param styleString
     * @return
     */
    private String parseStyleString(String styleString) {
        StringBuilder result = new StringBuilder();
        String regex = "((\\<font[^\\<]+\\>)(\\<[bius]\\>)*|(\\</[bius]\\>)*(\\</font\\>))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(styleString);
        String[] splitString = styleString.split(regex);
        if (matcher.find()) {
            String style = parseStyle(matcher.group());
            result.append(style);
        }
        String subtitleText = splitString[1];
        result.append(subtitleText);
        return result.toString();
    }

    /**
     * 解析sytle
     *
     * @param styleString
     * @return
     */
    private String parseStyle(String styleString) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        String regex = "\\<[bius]\\>|\\<font[^\\<]+\\>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(styleString);
        while (matcher.find()) {
            String style = matcher.group();
            if (style.startsWith("<font")) {

                //字体大小
                String sizeRegex = "size=\\\"\\d+\\\"";
                Pattern sizePattern = Pattern.compile(sizeRegex);
                Matcher sizeMatcher = sizePattern.matcher(styleString);
                if (sizeMatcher.find()) {
                    String group = sizeMatcher.group();

                    int start = group.indexOf("\"");
                    int end = group.lastIndexOf("\"");
                    String size = group.substring(start + 1, end);
                    result.append("\\fs" + size);
                }

                //字体
                String faceRegex = "face=\\\"\\S+\\\"";
                Pattern facePattern = Pattern.compile(faceRegex);
                Matcher faceMatcher = facePattern.matcher(styleString);
                if (faceMatcher.find()) {
                    String group = faceMatcher.group();

                    int start = group.indexOf("\"");
                    int end = group.lastIndexOf("\"");
                    String face = group.substring(start + 1, end);
                    result.append("\\fn" + face);
                }

                //字体颜色
                String colorRegex = "color=\\\"#\\S+\\\"";
                Pattern colorPattern = Pattern.compile(colorRegex);
                Matcher colorMatcher = colorPattern.matcher(styleString);
                if (colorMatcher.find()) {
                    String group = colorMatcher.group();

                    int start = group.indexOf("#");
                    int end = group.lastIndexOf("\"");
                    String color = group.substring(start + 1, end);
                    result.append("\\1c&H" + SubtitleUtil.convertBgrColor(color) + "&");
                }

            } else if (style.startsWith("<b>")) {
                result.append("\\b1");
            } else if (style.startsWith("<i>")) {
                result.append("\\i1");
            } else if (style.startsWith("<u>")) {
                result.append("\\u1");
            } else if (style.startsWith("<s>")) {
                result.append("\\s1");
            }
        }
        result.append("}");
        return result.toString();
    }

    @Override
    public boolean isFileSupported(String ext) {
        return ext.equalsIgnoreCase(getSupportFileExt());
    }

    @Override
    public String getSupportFileExt() {
        return "srt";
    }
}
