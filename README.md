# 每日笑话 (Joke of the Day)

## 项目介绍

"每日笑话"是一款轻松有趣的安卓应用，为用户提供每日更新的笑话内容。用户可以浏览不同类别的笑话，收藏喜欢的内容，并设置每日推送通知，开启一天的好心情。


### 项目背景

在忙碌的现代生活中，一个简短的笑话可以带来片刻轻松和愉悦。"每日笑话"项目旨在通过提供精选的优质笑话内容，为用户的日常生活增添一抹欢乐色彩。无论是早晨起床、午休时刻还是晚间放松，都可以打开应用，享受轻松一刻。

## 主要功能

- **每日笑话**: 每天推送一则新笑话，让您的一天从欢笑开始
- **分类浏览**: 按照不同类别（幽默、双关语、冷笑话、职场笑话等）浏览笑话内容
- **收藏功能**: 收藏喜欢的笑话，随时回顾
- **分享功能**: 一键分享笑话到社交媒体，与朋友共同欢乐
- **通知提醒**: 可设置每日提醒，定时收到新笑话推送
- **离线支持**: 缓存笑话数据，无网络也能阅读
- **夜间模式**: 支持深色主题，保护眼睛
- **文本大小调整**: 可根据个人阅读习惯调整文本大小


## 技术特点

- **MVVM架构**: 采用现代化的MVVM架构模式开发
- **Jetpack组件**: 使用LiveData、ViewModel、Room、Navigation等Jetpack组件
- **数据存储**: 结合Room数据库和SharedPreferences进行数据持久化
- **网络请求**: 使用Retrofit和协程进行网络操作
- **UI设计**: 遵循Material Design设计规范，提供流畅的用户体验
- **本地化**: 支持中文和英文界面
- **依赖注入**: 使用Hilt进行依赖注入
- **单元测试**: 包含关键功能的单元测试

### 技术栈

| 组件 | 技术 |
|------|------|
| UI框架 | Material Components |
| 导航 | Navigation Component |
| 网络 | Retrofit, OkHttp |
| 异步处理 | Kotlin Coroutines, Flow |
| 数据库 | Room |
| 图片加载 | Glide |
| 依赖注入 | Hilt |
| 测试 | JUnit, Espresso |


### 从源码构建

```bash
# 克隆仓库
git clone https://github.com/yourname/joke-of-the-day.git

# 进入项目目录
cd joke-of-the-day

# 使用Gradle构建
./gradlew assembleDebug
```

## 安装要求

- Android 8.0 (API级别26)及以上
- 约20MB存储空间
- 完整功能需要网络连接

## 隐私说明

本应用仅需要通知权限用于每日笑话推送功能。不会收集任何个人隐私数据。所有用户收藏的笑话仅存储在本地设备上。

详细隐私政策可查看[隐私政策页面](https://www.example.com/privacy)。

## 项目结构

```
joke_of_the_day/
├── data/                 # 数据层
│   ├── model/            # 数据模型
│   ├── repository/       # 数据仓库
│   ├── database/         # 本地数据库
│   ├── network/          # 网络相关
│   └── util/             # 工具类
├── ui/                   # 用户界面
│   ├── today/            # 今日笑话页面
│   ├── categories/       # 分类页面
│   ├── favorites/        # 收藏页面
│   └── settings/         # 设置页面
├── notification/         # 通知相关
└── util/                 # 通用工具类
```

## 开发和测试

1. 克隆项目到本地
2. 在Android Studio中打开项目
3. 构建并运行应用

### 调试模式

应用包含专门的调试选项，在设置页面连续点击版本号5次可以激活开发者选项。

## 贡献指南

我们欢迎任何形式的贡献！如果您想为项目做出贡献，请遵循以下步骤：

1. Fork项目仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m '添加了一些很棒的功能'`)
4. 将更改推送到分支 (`git push origin feature/amazing-feature`)
5. 开启一个Pull Request

更多详情请参阅[贡献指南文档](CONTRIBUTING.md)。

