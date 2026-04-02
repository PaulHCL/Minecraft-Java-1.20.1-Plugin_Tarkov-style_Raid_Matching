# Minecraft-Java-1.20.1-Plugin_Tarkov-style_Raid_Matching
Tarkov-style_Raid_Matching - A raid matching system for Minecraft 1.20.1 with multi-raid management, circular join points, 10s countdown teleport, and safe location pre-detection. Compatible with Paper/Spigot/Arclight.

# TarkovRaid - Minecraft 塔科夫风格战局匹配插件

##  项目简介 / Project Description


TarkovRaid 是一款专为 Minecraft 服务器设计的塔科夫风格战局匹配系统插件。玩家可以在指定区域设置战局，通过加入点确认进入战局区域，实现沉浸式 PVP/PVE 体验。支持多战局管理、安全传送、倒计时确认等功能。


TarkovRaid is a Tarkov-style raid matching system plugin designed for Minecraft servers. Players can set up raid zones and enter raid areas through join points with confirmation, creating an immersive PVP/PVE experience. Supports multi-raid management, safe teleportation, countdown confirmation, and more.

---

##  功能特性 / Features

| 中文 | English |
|------|---------|
| 🗺️ 多战局区域管理 | 🗺️ Multi-raid zone management |
| ⭕ 圆形范围加入点判定（5 格直径） | ⭕ Circular join point detection (5-block diameter) |
| ✅ 右键确认/左键取消机制 | ✅ Right-click confirm / Left-click cancel |
| ⏱️ 10 秒倒计时传送 | ⏱️ 10-second countdown teleportation |
| 🛡️ 安全位置预检测（避免传送到墙壁/虚空） | 🛡️ Safe location pre-detection (avoid walls/void) |
| 📝 配置文件外置消息（易于自定义） | 📝 External config messages (easy customization) |
| 🔒 ProGuard 代码混淆保护 | 🔒 ProGuard code obfuscation protection |
| 💾 数据自动保存（服务器重启不丢失） | 💾 Auto-save data (persistent after restart) |

---

##  安装说明 / Installation


1. 下载最新版本的 `TarkovRaid-protected.jar`
2. 将文件放入服务器 `plugins/` 文件夹
3. 重启服务器
4. 确保使用 **Paper/Spigot 1.20.1** 或 **Arclight/Mohist** 混合服务端


1. Download the latest `TarkovRaid-protected.jar`
2. Place the file in your server's `plugins/` folder
3. Restart the server
4. Ensure you're using **Paper/Spigot 1.20.1** or **Arclight/Mohist** hybrid server

---

##  使用指令 / Commands

| 指令 / Command | 说明 / Description | 权限 / Permission |
|----------------|-------------------|-------------------|
| `/traid` | 查看帮助信息 / View help | 无 / None |
| `/traid create <名称>` | 开始创建战局（第 1 点）/ Start creating raid (1st point) | `traid.admin` |
| `/traid create` | 继续创建战局（第 2-5 点）/ Continue creating raid (2nd-5th points) | `traid.admin` |
| `/traid confirm` | 完成创建并保存 / Finish and save | `traid.admin` |
| `/traid setjoin <战局名>` | 设置加入点 / Set join point | `traid.admin` |
| `/traid reload` | 重载配置文件 / Reload config | `traid.admin` |

---

##  快速开始 / Quick Start



**创建战局：**
```
1. 前往区域角落 1 → /traid create factory
2. 前往区域角落 2 → /traid create
3. 前往区域角落 3 → /traid create
4. 前往区域角落 4 → /traid create
5. 返回角落 1 → /traid create
6. 输入 → /traid confirm
```

**设置加入点：**
```
1. 站在想设置为加入点的方块上
2. 输入 → /traid setjoin factory
```

**玩家加入流程：**
```
1. 玩家走到加入点范围内（5 格直径）
2. 屏幕显示确认界面
3. 右键确认 → 10 秒倒计时
4. 倒计时结束 → 传送到战局区域
```



**Create Raid:**
```
1. Go to corner 1 → /traid create factory
2. Go to corner 2 → /traid create
3. Go to corner 3 → /traid create
4. Go to corner 4 → /traid create
5. Return to corner 1 → /traid create
6. Type → /traid confirm
```

**Set Join Point:**
```
1. Stand on the block you want as join point
2. Type → /traid setjoin factory
```

**Player Join Flow:**
```
1. Player walks into join point range (5-block diameter)
2. Confirmation UI appears
3. Right-click confirm → 10-second countdown
4. Countdown ends → Teleport to raid zone
```

---

##  配置文件 / Configuration

**config.yml 示例 / Example:**
```yaml
# ============================================
# TarkovRaid 插件配置文件 / Configuration File
# ============================================
# 修改后请使用 /traid reload 重载配置
# After modifying, use /traid reload to apply changes
# ============================================

# ============================================
# 消息设置 / Message Settings
# ============================================
messages:
  # 消息前缀（支持颜色代码 &）
  # Message prefix (supports color codes with &)
  prefix: "&6[TarkovRaid] "
  
  # 帮助信息标题
  # Help message title
  help_title: "=== 塔科夫战局系统 ==="
  
  # 帮助信息 - 创建指令
  # Help message - Create command
  help_create: "&e/traid create <战局名> - 开始设置 (第 1 点)"
  
  # 帮助信息 - 继续创建
  # Help message - Continue creating
  help_create_continue: "&e/traid create - 继续设置 (第 2-5 点)"
  
  # 帮助信息 - 确认指令
  # Help message - Confirm command
  help_confirm: "&e/traid confirm - 结束设置并保存"
  
  # 帮助信息 - 设置加入点
  # Help message - Set join point
  help_setjoin: "&e/traid setjoin <战局名> - 设置脚下为加入点"
  
  # 错误：只能在游戏内使用
  # Error: Console only
  console_only: "&c 请在游戏中使用此指令。"
  
  # 错误：没有权限
  # Error: No permission
  no_permission: "&c 你没有权限。"
  
  # 错误：首次创建需要名称
  # Error: First create needs name
  first_create_name: "&c 首次输入需提供战局名称：/traid create <名称>"
  
  # 错误：战局已存在
  # Error: Raid exists
  raid_exists: "&c 该战局名称已存在！"
  
  # 错误：战局不存在
  # Error: Raid not found
  raid_not_found: "&c 该战局不存在。"
  
  # 错误：没有进行中的设置
  # Error: No session
  no_session: "&c 当前没有进行中的设置。"
  
  # 错误：点数不足
  # Error: Not enough points
  not_enough_points: "&c 未完成 5 点记录，无法保存。"
  
  # 错误：点数已足够
  # Error: Points enough
  points_enough: "&c 点数已足够，请使用 /traid confirm 保存。"
  
  # 错误：验证失败
  # Error: Verify failed
  verify_failed: "&c 验证失败！第 5 次输入必须站在第 1 个点上！"
  
  # 成功：记录点位
  # Success: Point recorded (参数：{0}=当前点数)
  point_recorded: "&a[{0}/5] 已记录第 {0} 点。继续或返回起点。"
  
  # 成功：最终点位
  # Success: Final point
  point_final: "&a[5/5] 验证通过！区域闭合。请输入 /traid confirm 结束设置。"
  
  # 成功：战局创建
  # Success: Raid created (参数：{0}=战局名)
  raid_created: "&a 战局 [{0}] 创建成功！"
  
  # 错误：创建失败
  # Error: Create failed
  raid_create_failed: "&c 创建失败，请检查控制台。"
  
  # 成功：加入点设置
  # Success: Join point set (参数：{0}=战局名)
  join_point_set: "&a 已将脚下方块设置为 [{0}] 的加入点。"
  
  # 成功：配置重载
  # Success: Config reloaded
  config_reloaded: "&a 配置已重载。"
  
  # 成功：传送消息
  # Success: Teleport message (参数：{0}=战局名)
  teleport_message: "&e 你已潜入战局区域：{0}"

# ============================================
# 传送设置 / Teleport Settings
# ============================================
teleport:
  # 安全位置最大尝试次数
  # 如果战局区域地形复杂，可以增加此数值
  # Max attempts to find safe location
  # Increase this value if raid zone has complex terrain
  max_attempts: 50
  
  # 最小 Y 坐标（传送不会低于此值）
  # Minimum Y coordinate (teleport won't go below this)
  min_y: -64
  
  # 最大 Y 坐标（传送不会高于此值）
  # Maximum Y coordinate (teleport won't go above this)
  max_y: 319
  
  # 倒计时时间（秒）
  # 玩家右键确认后到传送的等待时间
  # Countdown time in seconds
  # Wait time between confirm and teleport
  countdown_seconds: 10
  
  # 传送冷却时间（毫秒）
  # 玩家传送后需要等待多久才能再次传送
  # Teleport cooldown in milliseconds
  # Wait time before player can teleport again
  cooldown_ms: 2000

# ============================================
# 加入点设置 / Join Point Settings
# ============================================
join_point:
  # 加入点判定半径（格）
  # 以加入点方块为中心，此半径内的圆形区域都有效
  # 2.5 = 直径 5 格
  # Join point detection radius in blocks
  # Circular area centered on join point block
  # 2.5 = 5 block diameter
  detection_radius: 2.5
  
  # 是否显示加入点粒子效果
  # 开启后会在加入点方块周围显示粒子
  # Show particle effects around join points
  # Particles will appear around join point blocks when enabled
  show_particles: true
  
  # 粒子类型（可选：VILLAGER_HAPPY, END_ROD, FLAME, etc.）
  # 完整列表：https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html
  # Particle type
  # Full list: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html
  particle_type: "VILLAGER_HAPPY"
  
  # 粒子显示间隔（tick，20tick=1 秒）
  # 数值越小粒子越密集，但性能消耗越大
  # Particle display interval in ticks (20 ticks = 1 second)
  # Lower value = denser particles, but higher performance cost
  particle_interval_ticks: 40

# ============================================
# 标题设置 / Title Settings
# ============================================
title:
  # 确认界面 - 主标题
  # Confirmation UI - Main title
  confirm_main: "&e 请确认是否加入战局"
  
  # 确认界面 - 副标题
  # Confirmation UI - Subtitle
  confirm_sub: "&7 战局：§b{0}\n&f 右键确认 | 左键取消"
  
  # 确认界面 - 淡入时间（tick）
  # Confirmation UI - Fade in time (ticks)
  confirm_fade_in: 10
  
  # 确认界面 - 持续时间（tick）
  # Confirmation UI - Stay time (ticks)
  confirm_stay: 100
  
  # 确认界面 - 淡出时间（tick）
  # Confirmation UI - Fade out time (ticks)
  confirm_fade_out: 10
  
  # 倒计时界面 - 主标题
  # Countdown UI - Main title
  countdown_main: "&e§l 准备传送"
  
  # 倒计时界面 - 副标题（参数：{0}=战局名，{1}=剩余秒数）
  # Countdown UI - Subtitle (args: {0}=raid name, {1}=seconds left)
  countdown_sub: "&7 战局：§b{0}\n&f§l{1}"
  
  # 倒计时界面 - 持续时间（tick）
  # Countdown UI - Stay time (ticks)
  countdown_stay: 20
  
  # 取消界面 - 主标题
  # Cancel UI - Main title
  cancel_main: "&c§l 传送取消"
  
  # 取消界面 - 副标题
  # Cancel UI - Subtitle
  cancel_sub: "&7 你已离开加入点范围"
  
  # 成功界面 - 主标题
  # Success UI - Main title
  success_main: "&a§l 传送成功"
  
  # 成功界面 - 副标题
  # Success UI - Subtitle
  success_sub: "&7 你已进入战局区域"

# ============================================
# 调试设置 / Debug Settings
# ============================================
debug:
  # 是否启用调试模式
  # 开启后会在控制台输出详细信息
  # Enable debug mode
  # Detailed information will be printed to console when enabled
  enabled: false
  
  # 是否记录传送失败原因
  # Log teleport failure reasons
  log_teleport_failures: true
```

---

##  文件结构 / File Structure

```
TarkovRaid
├─ pom.xml
├─ TarkovRaid.iml
├─ src
│  └─ main
│     ├─ resources
│     │  ├─ config.yml
│     │  └─ plugin.yml
│     └─ java
│        └─ com
│           └─ tarkov
│              └─ raid
│                 ├─ CommandHandler.java
│                 ├─ JoinListener.java
│                 ├─ MessageManager.java
│                 ├─ RaidManager.java
│                 └─ TarkovRaidPlugin.java
└─ .idea
   ├─ .gitignore
   ├─ compiler.xml
   ├─ encodings.xml
   ├─ jarRepositories.xml
   ├─ misc.xml
   ├─ modules.xml
   └─ workspace.xml

```

---

##  注意事项 / Notes

| 中文 | English |
|------|---------|
| 仅支持 **Java 17+** 服务器 | Only supports **Java 17+** servers |
| 不兼容纯 **Forge** 服务端 | Not compatible with pure **Forge** servers |
| 纯 **Bukkit/Spigot/Paper** 推荐使用 | Pure **Bukkit/Spigot/Paper** recommended |
| 如需 Forge 模组支持，请使用 **Arclight/Mohist** | For Forge mod support, use **Arclight/Mohist** |
| 战局区域至少需要 2 格高度空间 | Raid zones need at least 2-block height space |
| 加入点范围不可重叠（避免冲突） | Join point ranges should not overlap (avoid conflicts) |

---

##  问题反馈 / Issue Reporting


如遇到问题，请在 GitHub Issues 中提供以下信息：
- 服务器类型和版本
- 插件版本
- 控制台错误日志
- 复现步骤


If you encounter issues, please provide the following in GitHub Issues:
- Server type and version
- Plugin version
- Console error logs
- Steps to reproduce

---

##  许可证 / License

本项目采用 MIT 许可证  
This project is licensed under the MIT License


---

##  开发计划 / Roadmap

- [ ] 撤离点系统 / Extraction point system
- [ ] 战局计时器 / Raid timer
- [ ] 队伍匹配 / Team matching
- [ ] 经济系统整合 / Economy integration
- [ ] Web 管理面板 / Web admin panel

---

<div align="center">

**如果这个插件对你有帮助，请给个 ⭐ Star！**  
**If this plugin helps you, please give it a ⭐ Star!**

</div>
