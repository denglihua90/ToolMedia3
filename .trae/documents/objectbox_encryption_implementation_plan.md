# ObjectBox 加密数据库实现计划

## 1. 项目现状分析

### 当前数据库实现
- 已使用 Room + SQLCipher 实现了加密数据库
- 包含 6 个数据实体：VideoHistory、PlaybackProgress、DownloadTask、VideoFavorite、UserSetting、CacheInfo
- 对应的数据访问对象 (DAO) 已实现
- 数据库管理器已配置加密支持

### 兼容性问题
- 之前尝试使用 ObjectBox 时遇到 AGP 9.0.0 兼容性问题
- 需要重新评估 ObjectBox 与当前 Android Gradle Plugin 版本的兼容性

## 2. ObjectBox 加密功能分析

### 加密特性
- ObjectBox 原生支持数据库加密
- 使用 AES-256 加密算法
- 性能优于传统 SQLite 加密方案
- 配置简单，集成方便

### 优势
- 高性能：比 SQLCipher 更快
- 低内存占用：适合移动设备
- 原生支持：无需额外依赖
- 跨平台：支持 Android、iOS、桌面端

## 3. 实施步骤

### 步骤 1：移除现有 Room + SQLCipher 实现
- 移除 app/build.gradle.kts 中的 Room 和 SQLCipher 依赖
- 删除现有的 Room 相关文件：
  - 所有实体类 (VideoHistory.kt, PlaybackProgress.kt, DownloadTask.kt, VideoFavorite.kt, UserSetting.kt, CacheInfo.kt)
  - 所有 DAO 接口
  - AppDatabase.kt
  - DatabaseManager.kt

### 步骤 2：集成 ObjectBox
- 在 project-level build.gradle.kts 中添加 ObjectBox 插件
- 在 app/build.gradle.kts 中添加 ObjectBox 依赖
- 配置 ObjectBox 插件

### 步骤 3：创建 ObjectBox 实体类
- 为每个数据模型创建 ObjectBox 实体
- 使用 @Entity 注解标记实体类
- 为每个实体定义主键和属性

### 步骤 4：实现 ObjectBox 加密配置
- 配置 ObjectBox 数据库加密
- 设置加密密钥
- 实现数据库初始化和加密管理

### 步骤 5：创建 ObjectBox 数据库管理器
- 实现 ObjectBox 数据库实例管理
- 提供加密配置方法
- 实现单例模式管理数据库实例

### 步骤 6：测试加密功能
- 验证数据库加密是否正常工作
- 测试数据读写性能
- 确保加密数据库的安全性

## 4. 数据迁移策略

### 从 Room 迁移到 ObjectBox
- 实现数据导出工具
- 导出 Room 数据库中的现有数据
- 导入到 ObjectBox 数据库

## 5. 风险评估

### 潜在问题
- AGP 9.0.0 兼容性问题
- 数据迁移复杂性
- 性能影响评估

### 缓解措施
- 检查 ObjectBox 最新版本的兼容性
- 制定详细的数据迁移计划
- 进行性能测试和优化

## 6. 预期成果

- 成功集成 ObjectBox 加密数据库
- 实现与现有功能相同的数据模型
- 提供高性能的加密数据库解决方案
- 确保数据安全性和应用稳定性

## 7. 后续步骤

- 实施计划中的各个步骤
- 测试加密功能和性能
- 验证数据迁移是否成功
- 部署到生产环境