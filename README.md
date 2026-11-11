# dream120 — TMS120 伺服器原始碼專案

由 `dist\TMS120.jar`（v120 繁體伺服器）反編譯還原而成的完整可編譯專案。

## 來源與致謝

本專案的**程式碼主體是將 `TMS120.jar` 反編譯後合併而成**，專案架構則沿用 dreamMS120：

- **程式碼主體**：`TMS120.jar` 反編譯（反編譯與架設流程參考[楓之谷單機 V120 伺服架設一鍵安裝](https://www.youtube.com/watch?v=dZ_HLgqxVcQ&t=165s)）。`src/` 就是反編譯後修正到可編譯的結果（324 個 `.java`／754 個 `.class`），並非直接使用 dreamMS120 的原始碼。
- **專案架構／建置**：[v3921358/dreamMS120](https://github.com/v3921358/dreamMS120)（目錄結構、`Launcher.bat`、`lib` 依賴與腳本配置）。

合併後另外處理了：CFR 反編譯造成的型別／語法問題、以 JDK 8 重新編譯（754 個 class），以及購物商城 error code 38 的 `MapleAESOFB.crypt` 修正（詳見下方「修正紀錄」）。

## 環境

- IntelliJ IDEA Community 2023.2.1
- JDK 8（`C:\Program Files\Java\jdk1.8.0_202`，IDEA 內的 SDK 名稱：`1.8`）
- 專案語言等級：JDK 1.8 / bytecode target 1.8

## 目錄結構

```
dream120/
├─ src/                 # 324 個 .java（754 個 class，與原 JAR 一致）
├─ lib/                 # 編譯期依賴（mina、mysql-connector、slf4j 等）
├─ dist/
│  ├─ TMS120.jar        # 已編譯並打包的伺服器 JAR（Main-Class: server.Start）
│  └─ lib/              # 執行期依賴
├─ Libs/                # 腳本（NPC／quest／event 等 .js）
├─ sql/                 # 資料庫結構
├─ Settings.ini         # 伺服器設定
├─ Launcher.bat         # 啟動腳本
├─ wz/                  # 指向原伺服器 wz 資料夾的 junction（不重複佔用 883MB）
└─ dream120.iml / .idea # IntelliJ 專案設定
```

## 建置

### 方式一：IntelliJ IDEA

1. `File → Open`，選擇 `C:\Users\ry438\Downloads\v120\dream120`。
2. 確認 `File → Project Structure → Project` 的 SDK 為 `1.8`、語言等級為 `8`。
3. `Build → Build Project`（或 `Ctrl+F9`）。

輸出位置：`out\production\dream120`。

### 方式二：命令列

```bat
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
dir /s /b src\*.java > sources.txt
"%JAVA_HOME%\bin\javac.exe" -encoding UTF-8 -source 8 -target 8 -nowarn ^
  -cp "lib\*" -d out\production\dream120 @sources.txt
```

## 執行

在專案根目錄（需具備 `wz`、`Libs`、`Settings.ini` 與 MySQL 資料庫）：

```bat
java -Xmx512M -server -Dnet.sf.odinms.wzpath=wz -cp "dist\TMS120.jar;dist\lib\*" server.Start
```

或直接執行 `Launcher.bat`。IntelliJ 也已內建執行設定 **TMS120 Server**。

> `wz` 是目錄 junction，指向
> `C:\Users\ry438\Downloads\v120\v120伺服安裝\MMSv120伺服器\wz`。
> 若要搬移專案，請把該 wz 資料夾複製過來，或重新建立 junction。

## Java 21 實驗分支（`java21`）

`main` 分支維持 JDK 8（穩定的版本）。`java21` 分支是「在 Java 21 上跑」的概念驗證（POC），
**不需要修改任何伺服器邏輯程式碼**，只補上腳本引擎與一個腳本修正。

### 為什麼可以幾乎無痛升級

TMS120 的程式碼與 2499 個腳本都沒有用到 Java 9+ 移除的 API：

| 檢查項目 | 結果 |
|---|---|
| `SecurityManager`、`sun.misc.*`、`Thread.stop`、`javax.xml.bind`、`finalize()` | 0 處 |
| 腳本使用 `Java.type` / `Java.extend` / `JavaImporter`（GraalJS 才需要改寫的語法） | 0 處 |
| 腳本使用 `importPackage` / `Packages.*`（Nashorn 專屬語法） | 156 處 |
| 依賴 Nashorn 的原始碼 | 只有 `AbstractScriptManager` 的 `getEngineByName("nashorn")` |

因為腳本大量使用 `importPackage` / `Packages.*`，**不能改用 GraalJS**（會需要改寫全部腳本）。
本分支採用 [openjdk/nashorn](https://github.com/openjdk/nashorn)（Nashorn 的官方延續版），
它會註冊同名的 `nashorn` 引擎，因此 `AbstractScriptManager` 一行都不用改。

### 分支內容

```
lib/ 與 dist/lib/ 新增（執行期需要）
├─ nashorn-core-15.4.jar        # Nashorn 引擎（Java 15+ 的替代品）
├─ asm-7.3.1.jar                # nashorn-core 的相依
├─ asm-analysis-7.3.1.jar
├─ asm-commons-7.3.1.jar
├─ asm-tree-7.3.1.jar
└─ asm-util-7.3.1.jar

Launcher-java21.bat             # Java 21 啟動腳本（可用 JAVA21_HOME 指定 JRE）
Libs/scripts/npc/2040043.js     # 修正：見下方「大陣列字面量」
```

### 執行（Java 21）

```bat
set JAVA21_HOME=C:\path\to\jdk-21
Launcher-java21.bat
```

等同於：

```bat
java -Xmx512M -Dnet.sf.odinms.wzpath=wz -Dfile.encoding=UTF8 ^
  -cp ".;dist\TMS120.jar;dist\lib\*" server.Start
```

> classpath 必須明確寫出 `dist\lib\*`。`TMS120.jar` 的 manifest `Class-Path` 不含新加入的 jar，
> 只寫 `dist\*` 也不會包含子目錄。

### 相容性驗證

| 項目 | 結果 |
|---|---|
| 啟動（Corretto 21.0.12.1） | 正常：8484／8586／8587／8596 全部 listening，無例外 |
| MySQL（`mysql-connector-java` 5.1.12） | 正常，不需換 driver |
| Apache MINA 1.1.7 | 正常（常數池掃描無 `sun.misc` 等移除 API） |
| 2499 個 `.js` 腳本逐一 eval | Java 21：**2491 成功 / 8 失敗**，與 JDK 8 **完全相同的失敗清單** |

### 大陣列字面量（`npc/2040043.js`）

這是 Java 21 唯一多出來的失敗：`AssertionError: Failed generating bytecode for <eval>:136`。

`npc/2040043.js`（LudiPQ 8 階段的藍色氣球）用一個 126 × 9 的 `Array(Array(...), ...)` 巢狀字面量
直接寫在單一運算式裡。JDK 8 的 Nashorn 勉強能編譯，新版 Nashorn（15.4／15.7）的位元組碼產生器
在這種大小會失敗。

修正：把同一個陣列拆成 3 段 `concat`（資料完全相同、順序不變，仍是整數陣列）：

```js
var stage8combos = Array(
  Array(0,0,0,0,1,1,1,1,1),
  ...  // 前 42 列
);
stage8combos = stage8combos.concat(Array(
  ...  // 中 42 列
));
stage8combos = stage8combos.concat(Array(
  ...  // 後 42 列
));
```

修正後 Java 21 的腳本測試結果與 JDK 8 一致（2491 / 2499，剩下 8 個是原本就存在的
Big5 編碼／殘缺語法問題，兩個 JDK 都一樣失敗）。

### 尚未處理（不影響啟動）

- `local_policy.jar` / `US_export_policy.jar`：Java 8 時代的 JCE 政策檔，Java 21 不需要，
  但保留也不會出錯。
- `mysql-connector-java` 5.1.12、`slf4j` 1.5.0、`mina-core` 1.1.7 皆維持原版本（實測可用）。
- 尚未升級為 Java 21 語法（record／switch expression）或模組化；本分支目標只是「能跑」。

## 還原說明

- **反編譯來源影片**：[楓之谷單機 V120 伺服架設一鍵安裝](https://www.youtube.com/watch?v=dZ_HLgqxVcQ&t=165s)（`TMS120.jar` 反編譯與架設流程）
- 反編譯工具：CFR 0.152。
- 已修正 CFR 產生的型別與語法問題（`int` → `short`/`byte` 建構子轉型、`WeakReference`/`Iterator`/`Map.Entry` 泛型、`AdminCommand` 內部 `Map` 與 `java.util.Map.Entry` 的名稱衝突、未初始化區域變數、破損迴圈與 `goto` 等）。
- 以 JDK 8 編譯結果：**0 error，754 個 .class**，與原 `TMS120.jar` 的 class 數量相同。
- 反編譯程式碼與原始位元組碼在語意上可能仍有極細微差異；若有特定功能異常，請以該方法為重點比對。

## 注意

- 本專案不含 `wz` 圖檔資料（以 junction 連結原伺服器資料）。
- 資料庫連線設定在 `Settings.ini`，預設 `jdbc:mysql://localhost:3306/MapleStory`。

## 修正紀錄

### 購物商城 error code 38（`MapleAESOFB.crypt` 加密錯誤）

症狀：其他功能正常，但進入購物商城時客戶端出現 **error code 38**（Reached the end of the file）。

原因：`src/tools/MapleAESOFB.java` 的 `crypt()` 迴圈更新順序錯誤。原本在 `start += llength` 後先把
`llength` 設為 1460，`for` 的更新式才執行 `remaining -= llength`，等於少扣 4 bytes。
因此**每個大於 1456 bytes 的封包，最後 4 bytes 沒有被加密**；41 KB 的 `CS_OPEN` 封包尾端被客戶端
解讀成錯誤的數量，讀到封包結尾就發生 EOF。

修正：改為 `while (remaining > 0)`，順序為

```java
start += llength;
remaining -= llength;
llength = 1460;
```

修正後與原廠 `TMS120.jar` 的加密封包輸出在 1456 / 1457 / 1500 / 5000 / 41566 bytes 皆 byte-for-byte 相同。


## 常見問題：scripts 載入失敗、傳送點／NPC 無反應

症狀：伺服器主控台大量出現

```
Error executing script. Path: Libs/scripts/...
Exception java.lang.NullPointerException: Cannot invoke "javax.script.ScriptEngine.eval(String)" because ... is null
```

原因：伺服器不是用 Java 8 啟動。`AbstractScriptManager` 使用
`ScriptEngineManager().getEngineByName("nashorn")`，而 Nashorn 在 Java 15 之後已被移除，
所以 `engine` 為 `null`，所有 `.js` 腳本（傳送點、NPC、事件）都會失敗。

解法：`Launcher.bat` 已改為明確使用 JDK 8：

```
C:\Program Files\Java\jdk1.8.0_202\bin\java.exe
```

若你的系統 `PATH` 中的 `java` 是 17／21，直接執行 `java ... server.Start` 就會踩到這個問題。
請關閉舊的伺服器視窗，改用本專案的 `Launcher.bat` 重新啟動。

若要在 Java 21 上執行，請改用 `java21` 分支（已內建 `nashorn-core` 與 `Launcher-java21.bat`），
詳見上方「Java 21 實驗分支」。
