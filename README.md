# LCG
开源吾爱破解论坛APP

UI参考了知乎、虎扑App及头条系各app的展示, 架构参考了Google官方的MVVM代码结构.
代码基本以kotlin为主，还有一些历史代码会逐渐切换到kotlin上来。

论坛讨论贴: https://www.52pojie.cn/thread-1325357-1-1.html

## 主要支持功能
- 浏览52论坛各板块
- 支持账号密码方式登陆（存在偶尔需要重新登陆问题，待修复）
- 支持评论、点赞、收藏帖子
- 支持关注
- 支持阅读记录，避免错过内容
- 支持自动签到

APK下载 蓝奏云
 - [V1.8.7](https://fabirce.lanzoux.com/i46Z5j7scab)
 
## 相关技术
- 使用最新流行的[Coil](https://coil-kt.github.io/coil/)替换了Glide
- 参考并重写了[HtmlTextView](https://github.com/SufficientlySecure/html-textview)
- 做了App崩溃的自动重启，请参考代码AppGuard
