/** 一覧表示用の回想アイテム(回想番号・タイトル・合戦場・必要刀剣男士・発生条件) */
export interface RecollectionListItem {
  id: number
  number: number
  title: string
  battlefieldName: string | null
  /** 合戦場の時代区分。同名の合戦場(例:鳥羽)を画面上で区別する際に使う。 */
  battlefieldEra: string | null
  swordNames: string[]
  condition: string | null
  /** condition内に「ボス」関連の語を含むかどうかの補助フラグ。正確な条件は condition を参照する。 */
  bossRequired: boolean | null
}

/** 詳細表示用の回想アイテム(一覧項目 + 前提回想・備考) */
export interface RecollectionDetail extends RecollectionListItem {
  prerequisiteTitle: string | null
  remarks: string | null
}

/**
 * 合戦場プルダウンの選択肢。
 * id は検索APIにそのまま渡す一意識別子(name だけでは「鳥羽」等が重複するため)。
 */
export interface BattlefieldOption {
  id: string
  name: string
  era: string | null
  mapNumber: string | null
}

export interface SwordOption {
  name: string
  school: string | null
  type: string | null
}
