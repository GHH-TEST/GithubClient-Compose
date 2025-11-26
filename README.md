# GithubClient-Compose

Test repository for GitHub Client




1. 测试账号 Token：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
2. 测试账号用户名：GHH-TEST
3. 测试仓库名称：[GithubClient-Compose](https://github.com/GHH-TEST/GithubClient-Compose)

  



### 使用指南

| 功能       | 操作步骤                                              |
| -------- | ------------------------------------------------- |
| 浏览热门仓库   | 首页「热门仓库」→ 滚动查看高星仓库 → 点击列表项进入详情页                   |
| 搜索仓库     | 首页「搜索仓库」→ 输入关键词 → 点击「搜索」→ 查看结果（支持加载更多）            |
| 登录       | 首页右上角「登录」→ 输入 GitHub 个人访问令牌（需含 `repo` 权限）→ 点击「登录」 |
| 个人主页     | 登录后首页点击用户名 → 查看个人仓库 → 支持「退出登录」                    |
| 查看仓库详情   | 点击仓库列表项 → 查看所有者、描述、Stars/Issues 数            |
| 提交 Issue | 登录且为仓库所有者 → 详情页「提交 Issue」→ 输入标题 → 提交              |

  



### 类介绍

* **MainActivity.kt**：应用主入口，初始化 ViewModel 和导航控制器，管理全局状态与路由配置，承载所有屏幕组件。
* **HomeScreen.kt**：首页，展示应用名称和三大功能入口（热门仓库、搜索仓库、个人主页），适配登录 / 未登录状态。
* **MyPage.kt**：个人主页，登录后访问，展示个人仓库列表，支持退出登录。
* **HotReposScreen.kt**：热门仓库列表页，加载高星仓库，支持滚动加载更多
* **SearchReposScreen.kt**：仓库搜索页，含返回按钮、搜索框、结果列表，支持关键词检索和加载更多。
* **RepoDetailScreen.kt**：仓库详情页，展示仓库完整信息，仓库所有者可提交 Issue。



