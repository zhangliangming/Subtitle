package com.zlm.subtitlelibrary.formats.ass;

import android.text.TextUtils;

import com.zlm.subtitlelibrary.entity.SubtitleInfo;
import com.zlm.subtitlelibrary.entity.SubtitleLineInfo;
import com.zlm.subtitlelibrary.formats.SubtitleFileReader;
import com.zlm.subtitlelibrary.util.SubtitleUtil;
import com.zlm.subtitlelibrary.util.TimeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: ass字幕读取器
 * @author: zhangliangming
 * @date: 2019-01-14 0:11
 **/
public class AssSubtitleFileReader extends SubtitleFileReader {
    /**
     * 样式集合
     */
    private Map<String, Style> mStyleMap = new HashMap<String, Style>();

    @Override
    public SubtitleInfo readInputStream(InputStream in) throws Exception {
        if (in != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in,
                    getDefaultCharset()));
            StringBuilder fileContentSB = new StringBuilder();
            String lineInfo = "";
            while ((lineInfo = br.readLine()) != null) {
                fileContentSB.append(lineInfo + "\n");
            }
            in.close();
            br.close();
            in = null;
            br = null;
            return readText(fileContentSB.toString(), null);
        }
        return null;
    }

    @Override
    public SubtitleInfo readText(String fileContentString, File saveFile) throws Exception {
        saveFile(saveFile, fileContentString);
        SubtitleInfo subtitleInfo = new SubtitleInfo();
        subtitleInfo.setDefaultCharset(getDefaultCharset());
        subtitleInfo.setExt(getSupportFileExt());

        List<SubtitleLineInfo> subtitleLineInfos = new ArrayList<SubtitleLineInfo>();

        String[] fileContents = fileContentString.split("\n");

        for (int i = 0; i < fileContents.length; i++) {
            parseSubtitleInfo(fileContents[i], subtitleLineInfos);
        }

        //设置字幕
        if (subtitleLineInfos != null && subtitleLineInfos.size() > 0) {
            subtitleInfo.setSubtitleLineInfos(subtitleLineInfos);
        }

        return subtitleInfo;
    }

    /**
     * 解析字幕
     *
     * @param subtitleLineString
     * @param subtitleLineInfos
     */
    private void parseSubtitleInfo(String subtitleLineString, List<SubtitleLineInfo> subtitleLineInfos) {
        if (subtitleLineString.startsWith("Style")) {
            parseStyle(subtitleLineString);
        } else if (subtitleLineString.startsWith("Dialogue")) {
            SubtitleLineInfo subtitleLineInfo = new SubtitleLineInfo();
            boolean flag = parseSubtitleTime(subtitleLineString, subtitleLineInfo);
            if (!flag) return;

            String subtitleString = parseSubtitleString(subtitleLineString);
            //分隔每行字幕
            String[] splitSubtitles = subtitleString.split("\\\\[N]");

            //加载字幕
            String subtitleHtmlString = "";
            String subtitleTextString = "";
            for (int i = 0; i < splitSubtitles.length; i++) {
                String temp = subtitleAddStyle(subtitleLineString, splitSubtitles[i]);
                String[] result = SubtitleUtil.parseSubtitleText(temp);
                subtitleTextString += result[0];
                subtitleHtmlString += result[1];

                if (i != splitSubtitles.length - 1) {
                    subtitleTextString += "\n";
                    subtitleHtmlString += "<br>";
                }
            }
            subtitleLineInfo.setSubtitleText(subtitleTextString);
            subtitleLineInfo.setSubtitleHtml(subtitleHtmlString);
            subtitleLineInfos.add(subtitleLineInfo);
        }
    }

    /**
     * 字幕内容添加文本样式
     *
     * @param subtitleLineString
     * @param subtitleString
     * @return
     */
    private String subtitleAddStyle(String subtitleLineString, String subtitleString) {
        String styleName = getStyleName(subtitleLineString);
        if (!TextUtils.isEmpty(styleName)) {
            Style style = mStyleMap.get(styleName);
            if (style != null) {
                subtitleString = subtitleString.replaceAll("\\{\\r\\}", style.getStyleString());

                //分隔出没有样式的字幕内容
                String regex = "\\{[^\\{]+\\}[^\\{]*";
                String[] splitSubtitles = subtitleString.split(regex, -1);
                int index = 0;
                StringBuilder subtitleTextSB = new StringBuilder();
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(subtitleString);
                //遍历样式字符串
                while (matcher.find()) {
                    if (index == 0 && splitSubtitles.length > 0 && !TextUtils.isEmpty(splitSubtitles[0])) {
                        subtitleTextSB.append(style.getStyleString() + splitSubtitles[0]);
                    }
                    String styleString = matcher.group();
                    if (index + 1 >= splitSubtitles.length) {
                        break;
                    }
                    subtitleTextSB.append(styleString);

                    index++;
                }
                //如果没有样式
                if (index == 0 && splitSubtitles.length > 0 && !TextUtils.isEmpty(splitSubtitles[0])) {
                    subtitleTextSB.append(style.getStyleString() + splitSubtitles[0]);
                }
                //添加剩余的字幕内容
                for (index++; index < splitSubtitles.length; index++) {
                    if (!TextUtils.isEmpty(splitSubtitles[index])) {
                        subtitleTextSB.append(style.getStyleString() + splitSubtitles[index]);
                    }
                }
                subtitleString = subtitleTextSB.toString();

            }
        }
        return subtitleString;
    }

    /**
     * 获取样式名称
     *
     * @param subtitleLineString
     * @return
     */
    private String getStyleName(String subtitleLineString) {
        String regex = "Dialogue\\S\\s+\\d+,\\d+:\\d+:\\d+.\\d+,\\d+:\\d+:\\d+.\\d+,\\S+,";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(subtitleLineString);
        if (matcher.find()) {
            String group = matcher.group();
            String[] splitGroupString = group.split(",");
            return splitGroupString[3];
        }
        return null;
    }

    /**
     * 解析style样式
     *
     * @param styleString
     */
    private void parseStyle(String styleString) {
        styleString = styleString.replaceAll("Style\\S\\s+", "");
        String[] splitStyles = styleString.split(",");

        Style style = new Style();
        style.setName(splitStyles[0]);
        style.setFontname(splitStyles[1]);
        style.setFontsize(splitStyles[2]);
        style.setPrimaryColour(splitStyles[3]);
        style.setBold(splitStyles[7]);
        style.setItalic(splitStyles[8]);
        style.setUnderline(splitStyles[9]);
        style.setStrikeout(splitStyles[10]);

        mStyleMap.put(style.getName(), style);
    }

    /**
     * 解析歌词
     *
     * @param subtitleLineString
     * @return
     */
    private String parseSubtitleString(String subtitleLineString) {
        String regex = "Dialogue\\S\\s+\\d+,\\d+:\\d+:\\d+.\\d+,\\d+:\\d+:\\d+.\\d+,\\S+,";
        return subtitleLineString.split(regex)[1];
    }

    /**
     * 解析字幕时间
     *
     * @param timeString
     * @param subtitleLineInfo
     */
    private boolean parseSubtitleTime(String timeString, SubtitleLineInfo subtitleLineInfo) {
        String regex = "\\d+:\\d+:\\d+.\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(timeString);
        if (matcher.find()) {
            int startTime = TimeUtil.parseSubtitleTime(matcher.group());
            subtitleLineInfo.setStartTime(startTime);
            if (matcher.find()) {
                int endTime = TimeUtil.parseSubtitleTime(matcher.group());
                subtitleLineInfo.setEndTime(endTime);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isFileSupported(String ext) {
        return ext.equalsIgnoreCase(getSupportFileExt());
    }

    @Override
    public String getSupportFileExt() {
        return "ass";
    }

    /**
     * 样式集合
     */
    private class Style {
        /**
         * 样式名称
         */
        private String name;
        /**
         * 字体名称
         */
        private String fontname;
        /**
         * 字体大小
         */
        private String fontsize;
        /**
         * 主体颜色
         */
        private String primaryColour;

        /**
         * 粗    体（ -1=开启，0=关闭）
         */
        private String bold;

        /**
         * 斜    体（ -1=开启，0=关闭）
         */
        private String italic;

        /**
         * 下划线 （ -1=开启，0=关闭）
         */
        private String underline;

        /**
         * 删除线（ -1=开启，0=关闭）
         */
        private String strikeout;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFontname() {
            return fontname;
        }

        public void setFontname(String fontname) {
            this.fontname = fontname;
        }

        public String getFontsize() {
            return fontsize;
        }

        public void setFontsize(String fontsize) {
            this.fontsize = fontsize;
        }

        public String getPrimaryColour() {
            return primaryColour;
        }

        public void setPrimaryColour(String primaryColour) {
            this.primaryColour = primaryColour;
        }

        public String getBold() {
            return bold;
        }

        public void setBold(String bold) {
            this.bold = bold;
        }

        public String getItalic() {
            return italic;
        }

        public void setItalic(String italic) {
            this.italic = italic;
        }

        public String getUnderline() {
            return underline;
        }

        public void setUnderline(String underline) {
            this.underline = underline;
        }

        public String getStrikeout() {
            return strikeout;
        }

        public void setStrikeout(String strikeout) {
            this.strikeout = strikeout;
        }

        public String getStyleString() {
            StringBuilder result = new StringBuilder();
            result.append("{");
            if (!TextUtils.isEmpty(fontname)) {
                result.append("\\fn" + fontname);
            }

            if (!TextUtils.isEmpty(fontsize)) {
                result.append("\\fs" + fontsize);
            }

            if (!TextUtils.isEmpty(primaryColour)) {
                result.append("\\1c&H" + (primaryColour.replaceAll("&H", "")) + "&");
            }

            if (!TextUtils.isEmpty(bold)) {
                result.append("\\b" + Math.abs(Integer.parseInt(bold)));
            }

            if (!TextUtils.isEmpty(italic)) {
                result.append("\\i" + Math.abs(Integer.parseInt(italic)));
            }

            if (!TextUtils.isEmpty(underline)) {
                result.append("\\u" + Math.abs(Integer.parseInt(underline)));
            }

            if (!TextUtils.isEmpty(strikeout)) {
                result.append("\\s" + Math.abs(Integer.parseInt(strikeout)));
            }

            result.append("}");
            return result.toString();
        }
    }
}
