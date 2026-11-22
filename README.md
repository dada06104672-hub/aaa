# Villager Power (Forge 1.20.1 / Create 0.5.1.x)

- 村人カプセル : 村人に右クリックで封入 / ブロックに右クリックで解放
- 回し車       : 村人入りカプセルで右クリックで投入 → 32RPM, 256SU(Create)。素手しゃがみ右クリックで取り出し
- 発電モーター : 回転力を消費してFEを発電。隣接するFE対応ブロックへ自動で出力

数値は src/main/java/com/example/villagerpower/Tuning.java で変更できます。

## jarのビルド
### A) 自分のPC (JDK 17 + Gradle 8.8)
    gradle build
    → build/libs/villagerpower-1.0.0.jar
### B) GitHub Actions
このフォルダをGitHubにpushすると .github/workflows/build.yml が自動でjarを作ります
(Actions → 該当run → Artifacts → villagerpower-jar)。
