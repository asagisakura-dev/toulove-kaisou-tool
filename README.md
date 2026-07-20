# 刀剣乱舞 回想回収支援ツール

審神者が「どの合戦場で・どの刀剣男士で回想が発生するか」を素早く確認できるようにするための
検索・一覧ツールです。トップページのみで完結するシンプルなMVP(最低限動作する版)です。

## 構成

```
kaisou-tool/
├── backend/   … Spring Boot (Java17) + SQLite による REST API
└── frontend/  … React + TypeScript + Material UI によるSPA
```

- Repository → Service → Controller の3層構成
- 一覧/詳細ともにDTOを介してAPIを公開(エンティティを直接返さない)
- 初期データは `backend/src/main/resources/data/*.json` で管理し、あとから自由に追加可能

## 画面イメージ

- 画面上部の検索エリアで「刀剣男士」「合戦場」をプルダウンから選択
  - 両方未選択 → 全回想一覧を表示
  - 片方のみ選択 → その条件のみで絞り込み
  - 両方選択 → AND検索
- 検索結果はカード形式の一覧で表示(回想番号・タイトル・合戦場・必要刀剣男士・発生条件)
- カードをクリックするとモーダルで詳細(前提回想・備考を含む)を表示

## 動作環境

- Java 17 以上
- Maven … 不要(同梱の Maven Wrapper `mvnw` / `mvnw.cmd` が自動でダウンロード・実行します。ローカルにMavenがある場合はそちらも利用可)
- Node.js 18 以上 / npm 9 以上

## 起動方法

### 1. バックエンド(Spring Boot)

Maven Wrapper(`mvnw` / `mvnw.cmd`)を同梱しているため、Maven未インストールでも実行できます
(初回実行時に `.mvn/wrapper/maven-wrapper.properties` の設定に従って Maven 本体が自動ダウンロードされます)。

```bash
cd backend

# macOS / Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

ローカルにMavenをインストール済みであれば、従来通り `mvn spring-boot:run` でも起動できます。

- 初回起動時、プロジェクト直下に `kaisou.db` (SQLite) が自動作成され、
  `src/main/resources/data/*.json` の内容が初期データとして投入されます。
- 起動後、`http://localhost:8080/api/recollections` にアクセスするとJSONが返れば起動成功です。

### 2. フロントエンド(React)

別ターミナルで:

```bash
cd frontend
npm install
npm run dev
```

- `http://localhost:5173` にアクセスすると画面が表示されます。
- バックエンドのURLを変更したい場合は `frontend/.env` に以下を設定してください。

```
VITE_API_BASE_URL=http://localhost:8080/api
```

## API仕様

| メソッド | パス | 説明 |
| --- | --- | --- |
| GET | `/api/recollections` | 全件取得 |
| GET | `/api/recollections?sword=一期一振` | 刀剣男士名で検索 |
| GET | `/api/recollections?battlefield=函館` | 合戦場**id**で検索(下記の通り name ではない点に注意) |
| GET | `/api/recollections?sword=一期一振&battlefield=函館` | AND検索 |
| GET | `/api/recollections/{id}` | 詳細取得(前提回想・備考を含む) |
| GET | `/api/swords` | プルダウン用の刀剣男士名一覧(読み仮名順、文字列配列) |
| GET | `/api/battlefields` | プルダウン用の合戦場一覧(ゲーム内順、`{id, name, era, mapNumber}` の配列) |

> **`battlefield` パラメータの値は合戦場の一意識別子(`id`)です。** 「鳥羽」のように同じ `name` が
> 複数の時代にまたがって存在するため、`name` では一意に絞り込めません。フロントエンドは
> `GET /api/battlefields` が返す `id` をそのまま検索条件として使っています。

## DB設計

```
recollections          battlefields          swords
├ id                    ├ id                  ├ id
├ number                ├ code (一意, JSONのid) ├ name (一意)
├ title                 ├ name (重複あり得る)   ├ reading (読み仮名, 空欄可)
├ battlefield_id (FK)   ├ era                 └ type
├ condition_text        ├ mapNumber (表示用)
├ boss_required         └ sortOrder (並び替え用。JSONには持たず起動時にmapNumberから自動計算)
├ prerequisite_id (FK, self)
└ remarks

recollection_swords (中間テーブル)
├ recollection_id (FK)
└ sword_id (FK)
```

- `battlefields.code` が一意識別子で、`recollections.battlefield_id` はこの行を内部的に(FKとして)直接参照します。
- `battlefields.name` は表示名であり、`code` と違って重複を許容します(「鳥羽」が2行存在するのはこのため)。

## データの追加方法

`backend/src/main/resources/data/` 配下の3つのJSONファイルを編集するだけで
データを追加・変更できます(**DBファイル `kaisou.db` を削除してから再起動**すると反映されます)。

### swords.json … 刀剣男士マスタ

```json
{ "name": "一期一振", "reading": "いちごひとふり", "type": "太刀" }
```

- `name`:刀剣男士名(一意)。`recollections.json` の `swords` 配列から参照される
- `reading`:読み仮名(ひらがな)。**五十音順ソートに使われる唯一の項目**。
  空欄のままだと一覧の末尾にまとめて表示される
- `type`:刀種。任意項目(現状は未入力)

### battlefields.json … 合戦場マスタ

```json
{ "id": "鳥羽(維新の記憶)", "name": "鳥羽", "era": "維新の記憶", "mapNumber": "1-4" }
```

- `id`:**一意識別子。`recollections.json` の `battlefield` から参照される値**。
  基本は `name` と同じでよいが、同名の合戦場が複数の時代にまたがる場合(例:鳥羽)は
  `"名前(時代)"` の形式にして重複を避ける
- `name`:画面表示用の合戦場名。重複可
- `era`:時代区分。ドロップダウンの見出し(グループ分け)にも使われる
- `mapNumber`:ゲーム内番号(例:`"1-1"`)。**並び替え順(sortOrder)はこの値から自動計算される**
  (章番号 × 1000 + マップ番号 × 10)。空欄・`"3-4"` 以外の形式の場合は一覧の末尾に回される。
  `sortOrder` は自動計算専用の内部値のためJSONには含めない

### recollections.json … 回想データ本体

```json
{
  "number": 32,
  "title": "九曜と竹雀のえにし 発端",
  "battlefield": "江戸（新橋）",
  "condition": "歌仙兼定と小夜左文字を一緒に組んでボスマス勝利(A～C判定問わず)、帰還後に発生(ボスマスで敗北した場合は発生せず)",
  "bossRequired": true,
  "swords": ["歌仙兼定", "小夜左文字"],
  "prerequisiteNumber": null,
  "remarks": ""
}
```

- `battlefield` は `battlefields.json` の **`id`** と一致させる(`name` ではない点に注意)
- `swords` は `swords.json` の `name` の配列
- `condition` はWikiの発生条件文をそのまま(省略・要約せず)保存する
- `bossRequired` は `condition` に「ボス」を含む場合 `true` とする補助フラグ。
  検索・表示で使えるが、**正確な条件は必ず `condition` を参照すること**
- `prerequisiteNumber` は前提となる回想の `number`(なければ `null`)

### 現在のデータについて

`swords.json` / `battlefields.json` / `recollections.json` は、提供いただいた
「回想データ.csv」「回想回収条件csv.csv」の2ファイルを突き合わせて生成した実データです
(回想193件・刀剣男士127件・合戦場33件〔鳥羽を時代ごとに分けたため32種類→33件〕)。生成にあたり、
以下の点にご注意ください。

- **`reading`(読み仮名)・`mapNumber`(ゲーム内番号)は、ご提供いただいたCSVの内容で全件反映済みです。**
  刀剣男士127件すべての読み仮名、合戦場33件中32件(「指定なし」を除く全件)のマップ番号が入力されており、
  五十音順ソート・ゲーム内順の並び替えがそのまま機能します。
- **CSVの刀剣名5件について、`recollections.json` 側の表記に合わせる形で補正しました。**
  誤字または表記ゆれと判断したもので、いずれもWeb検索で公式サイト等を確認のうえ補正しています。

  | CSV上の表記 | 採用した表記 |
  | --- | --- |
  | 古今伝授 | 古今伝授の太刀 |
  | 三郎国刀 | 三郎国宗 |
  | 太安宅 | 安宅切 |
  | 源清磨 | 源清麿 |
  | 童子切安綱 剥落 | 童子切安綱(特殊形態の表記は基本名に正規化する方針のため) |

  `reading` の値はCSVの該当行のものをそのまま採用しています。もし意図した表記と違う場合はお知らせください。
- **CSVにあった `school`(流派)列は、今回のJSONには反映していません。** スキーマ変更の依頼範囲外のため、
  `swords.json` は引き続き `name` / `reading` / `type` の3項目のみで構成しています。
- **`swords.json` の `type`(刀種)は今回のCSVの値で埋まりました。** 前回までは空欄でしたが、
  「太刀・打刀・短刀・脇差・大太刀・槍・薙刀・剣」の8種で全件入力済みです。
- **合戦場名の表記ゆれを一部統一しています。** CSV内に「関ケ原」「関ヶ原」という表記ゆれ(「ケ」の大小)
  があったため、検索が正しく機能するよう「関ヶ原」に統一しました。
- **「指定なし」も合戦場の一つとして扱っています。** 特定の合戦場に紐付かない回想(編成のみで発生するもの等)は
  `battlefield: "指定なし"` としており、`mapNumber` は空欄のまま一覧の末尾に表示されます。
- **`prerequisiteNumber` は前提回想が複数ある場合、先頭の1件のみを設定しています。**
  ほとんどの回想は前提が1件ですが、其の175のように3件の前提回想(其の84・85・86)が必要なケースなどでは
  構造上1件しか保持できません。正確な内容は必ず `condition` の全文を参照してください。
- **其の43は例外的に `bossRequired` を手動で `false` に補正しています。** 条件文に「ボス」という語自体は
  含まれるものの、「ボスマスで勝利する必要はない」と明記されているケースのため、単純な部分一致では
  誤判定になることを確認し、個別に補正しました。

## 今後の拡張候補(MVP範囲外)

- 回収済み/未回収のチェック管理(ローカル保存 or ユーザーアカウント)
- 刀種・時代での絞り込み
- `bossRequired` を使った検索フィルタ(現状は一覧・詳細への表示のみ対応)
- 回想の並び替え(番号順以外)
- 管理画面からのデータ編集(現状はJSON編集のみ)
