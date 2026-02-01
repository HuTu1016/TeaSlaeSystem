# 杀掉残留，解决 1412 错误
taskkill /f /im windsurf-cunzhi.exe 2>$null | Out-Null

# 启动并带上针对茶叶系统的快捷选项
$cmd = "D:\Windsurf\windows\windsurf-cunzhi.exe"
$args = "--ui", "--options", "前后端联调,完善管理端逻辑,生成测试数据,继续"

& $cmd $args 2>$null