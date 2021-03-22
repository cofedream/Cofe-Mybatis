# 前言

感谢 [JetBrains](https://www.jetbrains.com/?from=Cofe-Mybatis) 对此项目的支持。

# 相关地址 

[Gitee](https://gitee.com/cofedream/Cofe-Mybatis) | [GitHub](https://github.com/cofedream/Cofe-Mybatis) | [Jetbrains Marketplace](https://plugins.jetbrains.com/plugin/12808-cofe-mybatis)

# 介绍

本项目最早基于 [free-idea-mybatis](https://github.com/wuzhizhan/free-idea-mybatis) 修改，但因后续功能实现与原本差异较大，便于2018年11月份重新立项。

# 功能完成度

| 介绍 | 完成情况 |
| :---: | :---: |
| XMapper.java 跳转至 XMapper.XML | ✓ |

# 相关快捷键

| 说明| IDEA默认快捷键| 
| --- | --- | 
| 接口方法生成XML定义提示 | Alt+Enter |
| 接口方法跳转XML定义 | Ctrl+B/Ctrl+Alt+B |
| 接口方法生成@Param注解 | Alt+Enter |
| XML跳转接口 | Ctrl+B |
| XML中参数跳转定义 | Ctrl+B |
| XML中参数提示 | Ctrl+空格(Completion Base) |

# 功能演示

1. 支持Spring注入提示<br/> ![SpringInject](/images/SpringInject.gif)
2. 支持Mybatis接口与XMl互相跳转<br/> ![](/images/NavigateToXml.gif)<br/>![](/images/NavigateToMethod.gif)
3. 支持Mybatis接口中Statement<br/>![](/images/GenerateStatement.gif)
4. 支持Mybatis接口中@Param生成与XML代码提示<br/>![](/images/ParamCompletion.gif)
5. XML文件中的 sql或resultMap标签的 Documentation