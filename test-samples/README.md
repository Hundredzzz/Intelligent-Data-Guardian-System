# 测试样本使用说明

## 文本检测

打开员工账号：

```text
employee / 123456
```

进入“文本/文件检测”，复制 `01-text-copy-sample.txt` 中的正文到文本框，点击“文本检测”。

## 文件检测

在同一页面选择文件上传，可依次上传：

- `02-low-risk-public-notice.txt`
- `03-medium-risk-contact-info.txt`
- `04-high-risk-business-secret.txt`
- `05-critical-risk-bank-and-id.txt`

上传后点击“文件检测”。

## 审核流程

退出员工账号，使用主管账号：

```text
manager / 123456
```

进入“审核申请”，点击“查看审核”，查看原始内容、脱敏内容、风险命中、AI 建议，然后选择通过或驳回。

## 风险与安全员流程

使用数据安全员账号：

```text
security / 123456
```

进入“风险预警管理”查看高风险和严重风险任务；进入“AI审核”可单独测试 DeepSeek 审核。
