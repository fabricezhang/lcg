# LCG
第三方自制吾爱破解论坛APP
UI参照知乎和虎扑App, 架构参考Google官方MVVM代码结构.

论坛讨论贴: https://www.52pojie.cn/thread-1073834-1-1.html

APK下载 蓝奏云
 - [V1.6.4](https://www.lanzous.com/i88wy1i)
 - [V1.6.2](https://www.lanzous.com/i80uh1g)
 - [V1.5.0](https://www.lanzous.com/i6wmceb)
 - [V1.4.0](https://www.lanzous.com/i6tersj)
 - [V1.3.0](https://www.lanzous.com/i57tefa)
 
## 功能

### 1.6.4 版本更新
 - 个人页面支持查看消息通知/我的帖子
 - 收藏页面支持同步网络收藏
 - 跟帖功能提供了悬浮按钮（其实就是回复楼主）
 - 加载更多现在有了动画
 - BugFix

### 1.6.3 版本更新
 - 修复了大量的beta版本的错误，包括加载下一页失败，解析页面失败，收藏名称错误等
 - 增加了各板块的按时间排序功能（请看板块页面的右上角的三个点）
 - 增加了退出登陆的确认页面并优化了逻辑
 - 其他小优化

### V1.6.2 
 - 新增回复/支持
 - 同步收藏文章到网络
 - 长按预览文章
 - 消息通知查看
### v1.5.0
 - 通过Jsoup方式重新排版的文章浏览
 - 移动网页方式的浏览,支持账号密码登陆并保持登陆状态
 - 支持百度站内搜索(首页上方的搜索键）
 - 一键提取文章内所有百度/蓝奏下载链接并复制到剪贴板
 - 一键收藏文章并回看
 - 自动签到功能
 
## 未完工
 - QQ一键登陆功能
 - Jsoup方式部分文章的显示问题（代码展示被屏蔽/图片不能正确显示等问题）

涉及技术：
MVVM, databinding, livedata, RxJava, eventbus, jsoup, htmltext, lottie等
