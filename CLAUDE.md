# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

v2ray-core-wrapper 是一个 Kotlin 库，将 V2Ray CLI 包装为类型安全的 Kotlin 接口，同时将 V2Ray 的 JSON 配置结构映射为 Kotlin data class（通过 Jackson 进行序列化/反序列化）。

## 常用命令

```bash
# 编译项目
./gradlew build

# 仅编译源码
./gradlew compileKotlin

# 运行测试（需要 v2ray 可执行文件）
./gradlew test

# 运行单个测试类
./gradlew test --tests "com.kebab.v2rayk.wrapper.process.V2rayCliTest"

# 运行主类
./gradlew run

# 清理构建产物
./gradlew clean
```

> **注意**：测试依赖 `tmp/vcore/v2ray.exe`（V2Ray 核心可执行文件）和 `tmp/config.json`（测试用配置文件）。这些文件未纳入版本控制，需自行下载。
>
> 测试会实际启动 V2Ray 进程（`testV2rayTest` 中启动后等待 10 秒再停止），因此运行测试前需确保配置文件路径正确。

## 架构

### 包结构

```
com.kebab.v2rayk.wrapper
├── V2RayServer              # 接口：定义 V2Ray 服务生命周期
├── V2RayCliServer           # 实现：通过 ProcessBuilder 管理 V2Ray 子进程
├── config/
│   ├── V2rayProperties      # 顶层配置，对应 v2ray 完整 JSON
│   ├── Inbound              # 入站连接（端口、协议、监听地址、Sniffing、Allocate）
│   ├── Outbound             # 出站连接（sendThrough、协议、Mux、ProxySettings）
│   ├── Routing              # 路由规则（Rule、Balancer、域名策略、IP 匹配）
│   ├── Dns                  # DNS 配置（hosts 静态映射、servers 列表、DoH）
│   ├── Log                  # 日志（access/error 路径、loglevel 级别）
│   ├── Api                  # API 服务（HandlerService/LoggerService/StatsService）
│   ├── Policy               # 本地策略（按用户等级配置连接超时、bufferSize）
│   ├── Reverse              # 反向代理（Bridge 和 Portal 模式）
│   ├── Stats                # 统计信息（占位对象，统计由 Policy 中的开关控制）
│   └── bound/
│       ├── ProtocolType     # 枚举：VMess/Shadowsocks/SOCKS/HTTP/DNS/Freedom 等
│       ├── Settings         # 协议具体配置（Any? 占位，待按协议细化）
│       └── StreamSettings   # 传输层配置（Any? 占位，待按传输方式细化）
└── process/
    ├── V2RayCli             # CLI 参数构建器（拼接 version/test/run -c 等参数）
    └── FormatType           # 枚举：JSON / Protobuf
```

### 核心设计

**服务生命周期**：

- `V2RayServer` 接口定义了 `start(config)`、`start(configPath)`、`stop()`、`restart()`、`status()`、`getLogs()`
- `V2RayCliServer` 是其唯一实现，内部维护 `Process?` 引用，通过 `V2RayCli.toProcessBuilder().start()` 启动子进程，通过 `process.destroy()` 停止
- 后续可扩展为 gRPC/API 方式控制 V2Ray，而不只是 CLI

**配置映射策略**：

- 每个 data class 对应 V2Ray JSON 配置的一个节点，字段名与 JSON key 一致
- 使用 `@JsonValue` 注解控制枚举值的序列化格式（如 `ProtocolType.VMESS` → `"VMess"`）
- `V2rayProperties` 作为根配置对象，Jackson 序列化后即为合法的 v2ray 配置文件
- 字段注释直接翻译自 V2Ray 官方文档，保留中文

### 技术栈

| 依赖 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.1.0 | 语言 |
| Gradle | 8.5 | 构建系统 |
| jackson-module-kotlin | 2.15.2 | JSON 序列化 |
| zip4j | 2.11.5 | V2Ray 核心 zip 包解压 |
| kotlin-test | 1.8.10 | 测试框架（JUnit 5） |
| JVM Target | 21 | 编译目标 |

## 待完成项

- `V2RayCliServer` 中 `start(config: V2rayProperties)` 需实现：将 `V2rayProperties` 序列化为 JSON 临时文件，再调用 CLI 启动
- `restart()` 需保存当前配置引用
- `status()` 需读取进程状态，或调用 V2Ray API
- `getLogs()` 需根据 `Log.access`/`Log.error` 路径读取日志文件
- `Settings` 和 `StreamSettings` 目前为 `Any?`，需按各协议（VMess/Shadowsocks/SOCKS 等）分别定义具体 data class
