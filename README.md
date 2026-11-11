# dream120 — TMS120 伺服器原始碼專案

由 `dist\TMS120.jar`（v120 繁體伺服器）反編譯還原而成的完整可編譯專案。

## 來源與致謝

本專案是合併以下兩者而成：

- **伺服器架構**：[v3921358/dreamMS120](https://github.com/v3921358/dreamMS120)（dreamMS120 專案結構與程式碼基礎）
- **`TMS120.jar` 反編譯**：參考[楓之谷單機 V120 伺服架設一鍵安裝](https://www.youtube.com/watch?v=dZ_HLgqxVcQ&t=165s) 影片中的流程，將伺服器 JAR 反編譯後合併進此架構。

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
