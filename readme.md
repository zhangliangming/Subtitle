# 简介 #
[乐乐音乐播放器](https://github.com/zhangliangming/HappyPlayer5.git)最近添加了MV功能，由于部分MV没有字幕，所以这里为[乐乐音乐播放器](https://github.com/zhangliangming/HappyPlayer5.git)添加一个外挂字幕的开源库，目前该开源库主要简单支持srt和ass字幕文件。

# 字幕显示方式 #

    {\fn华文楷体\fs16\1c&H3CF1F3&\b1}影片壓制

转换

` <font color="#F3F13C"><b>影片壓制</b></font>`

显示方式主要是以html的方式显示，所以现在只支持读取字幕文本、html文本、颜色和加粗等基本功能，没有特效。

# 字幕格式解析 #

## 正则表达式 ##
- 时间标签

    `\d+:\d+:\d+,\d+`


- 分隔出每一项font标签

        <font color="#F3F13C"榮譽出品==--</font>
    
    (\<font[^\<]+\>)(\<[bius]\>)*[^\<]+(\</[bius]\>)*(\</font\>)

- 分隔出字幕内容

    ` <font color="#FF00FF"><b><u>http://cmct.cc</u></b></font>`

    ((\<font[^\<]+\>)(\<[bius]\>)*|(\</[bius]\>)*(\</font\>))

- 分隔ass


         Dialogue: 0,0:00:02.00,0:00:07.00,Default,,0000,0000,0001,,{\fn华文楷体\fs16\1c&H3CF1F3&\b0}--==本影片由 {\1c&HFF8000&\b1}CMCT 团队{\fn华文楷体\1c&H3CF1F3&\b0} 荣誉出品==--\N更多精彩影视 请访问 {\fnCronos Pro Subhead\1c&HFF00FF&\b1}http://cmct.cc{\r}

    Dialogue\S\s+\d+,\d+:\d+:\d+.\d+,\d+:\d+:\d+.\d+,\S+,


## srt字幕 ##

[SRT字幕的颜色以及一些特效的设置](http://www.360doc.com/content/17/0527/14/57493_657716572.shtml)


## ass字幕 ##
[ASS字幕格式规范](https://www.douban.com/note/658520175/)


# 预览图 #
## srt字幕 ##
![](https://i.imgur.com/SQMQBok.png)

## ass字幕 ##
![](https://i.imgur.com/MQ5xnUW.png)

# Gradle #

1.root build.gradle

	`allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}`
	
2.app build.gradle

   `dependencies {
	        implementation 'com.github.zhangliangming:Subtitle:v1.2'
	}`

# 混淆注意 #
-keep class com.zlm.subtitlelibrary.** { *; }

# 调用Demo #

链接: https://pan.baidu.com/s/1j-4wbtiNIfRhypb4uEnX6g 提取码: t8dj

# 声明 #

该项目的代码和内容仅用于学习用途

# 捐赠 #

如果该项目对您有所帮助，欢迎您的赞赏

- 微信

![](https://i.imgur.com/hOs6tPn.png)

- 支付宝

![](https://i.imgur.com/DGB9Lq0.png)