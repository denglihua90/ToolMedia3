# BaseUrl拦截器修复计划

## 分析问题

### 问题描述
- 当请求地址为 `https://api.guangsuapi.com/api.php/provide/vod/from/gsm3u8/?ac=list&t=13&h=24&pg=3` 时，被动态替换成了 `https://api.yzzy-api.com/api.php/provide/vod/from/gsm3u8/?ac=list&t=13&h=24&pg=3`
- 这导致请求被发送到错误的地址，可能导致请求失败

### 问题原因
- OkHttpClientManager中的BaseUrlInterceptor拦截器会在每个请求中动态替换baseUrl
- 拦截器会提取配置的baseUrl（默认为"https://api.yzzy-api.com/"）的scheme、host和port，然后替换原始URL中的这些部分
- 这导致即使使用完整的URL进行请求，也会被替换为配置的baseUrl

## 任务分解

### [ ] 任务1：修改BaseUrlInterceptor拦截器
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 修改BaseUrlInterceptor拦截器，使其只在必要时替换baseUrl
  - 当请求使用完整URL时，不进行baseUrl替换
- **Success Criteria**:
  - BaseUrlInterceptor能够正确处理完整URL请求
  - 当使用完整URL时，不会被替换为配置的baseUrl
- **Test Requirements**:
  - `programmatic` TR-1.1: 编译通过，无语法错误
  - `human-judgement` TR-1.2: 代码结构清晰，逻辑正确

### [ ] 任务2：测试修复效果
- **Priority**: P0
- **Depends On**: 任务1
- **Description**:
  - 构建并运行应用
  - 测试使用完整URL的请求是否不再被替换
  - 验证网络请求能够正常执行
- **Success Criteria**:
  - 使用完整URL的请求不再被替换
  - 网络请求能够正常执行
  - 应用能够成功获取数据
- **Test Requirements**:
  - `programmatic` TR-2.1: 构建成功
  - `human-judgement` TR-2.2: 网络请求正常工作，地址不再被错误替换

## 实现思路

1. **修改BaseUrlInterceptor拦截器**：
   - 检查请求是否使用完整URL
   - 如果是完整URL，不进行baseUrl替换
   - 如果是相对路径，使用配置的baseUrl

2. **测试修复效果**：
   - 构建并运行应用
   - 测试使用完整URL的请求
   - 验证网络请求能够正常执行

## 预期结果

- 使用完整URL的请求不再被替换为配置的baseUrl
- 网络请求能够正常执行，发送到正确的地址
- 应用能够成功获取数据
- 代码结构清晰，易于维护和扩展