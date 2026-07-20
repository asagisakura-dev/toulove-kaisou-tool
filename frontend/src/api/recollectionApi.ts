import { BattlefieldOption, RecollectionDetail, RecollectionListItem, SwordOption } from '../types'

interface RecollectionSeed {
  number: number
  title: string
  battlefield: string | null
  condition: string | null
  bossRequired: boolean | null
  swords: string[]
  prerequisiteNumber: number | null
  remarks: string | null
}

interface SwordSeed extends SwordOption {
  reading: string | null
}

const dataUrl = (filename: string) => `${import.meta.env.BASE_URL}data/${filename}`

async function fetchData<T>(filename: string): Promise<T> {
  const response = await fetch(dataUrl(filename))
  if (!response.ok) {
    throw new Error(`データの読み込みに失敗しました (status: ${response.status})`)
  }
  return response.json() as Promise<T>
}

let swordOptionsPromise: Promise<SwordOption[]> | undefined
let battlefieldOptionsPromise: Promise<BattlefieldOption[]> | undefined
let recollectionsPromise: Promise<RecollectionSeed[]> | undefined

function swords(): Promise<SwordOption[]> {
  swordOptionsPromise ??= fetchData<SwordSeed[]>('swords.json').then((items) =>
    [...items].sort((a, b) => {
      const readingOrder = (a.reading ?? '').localeCompare(b.reading ?? '', 'ja')
      return readingOrder || a.name.localeCompare(b.name, 'ja')
    }),
  )
  return swordOptionsPromise
}

function battlefields(): Promise<BattlefieldOption[]> {
  battlefieldOptionsPromise ??= fetchData<BattlefieldOption[]>('battlefields.json')
  return battlefieldOptionsPromise
}

function recollections(): Promise<RecollectionSeed[]> {
  recollectionsPromise ??= fetchData<RecollectionSeed[]>('recollections.json')
  return recollectionsPromise
}

function toListItem(recollection: RecollectionSeed, battlefieldById: Map<string, BattlefieldOption>): RecollectionListItem {
  const battlefield = recollection.battlefield ? battlefieldById.get(recollection.battlefield) : undefined
  return {
    id: recollection.number,
    number: recollection.number,
    title: recollection.title,
    battlefieldName: battlefield?.name ?? null,
    battlefieldEra: battlefield?.era ?? null,
    swordNames: [...(recollection.swords ?? [])].sort((a, b) => a.localeCompare(b, 'ja')),
    condition: recollection.condition,
    bossRequired: recollection.bossRequired,
  }
}

export async function fetchRecollections(
  sword?: string,
  battlefield?: string,
): Promise<RecollectionListItem[]> {
  const [items, battlefieldOptions] = await Promise.all([recollections(), battlefields()])
  const battlefieldById = new Map(battlefieldOptions.map((item) => [item.id, item]))

  return items
    .filter((item) => (!sword || item.swords?.includes(sword))
      && (!battlefield || item.battlefield === battlefield))
    .map((item) => toListItem(item, battlefieldById))
}

export async function fetchRecollectionDetail(id: number): Promise<RecollectionDetail> {
  const [items, battlefieldOptions] = await Promise.all([recollections(), battlefields()])
  const recollection = items.find((item) => item.number === id)
  if (!recollection) {
    throw new Error(`回想が見つかりません (id: ${id})`)
  }

  const listItem = toListItem(recollection, new Map(battlefieldOptions.map((item) => [item.id, item])))
  const prerequisite = recollection.prerequisiteNumber == null
    ? undefined
    : items.find((item) => item.number === recollection.prerequisiteNumber)

  return {
    ...listItem,
    prerequisiteTitle: prerequisite?.title ?? null,
    remarks: recollection.remarks,
  }
}

export function fetchSwordOptions(): Promise<SwordOption[]> {
  return swords()
}

export function fetchBattlefieldOptions(): Promise<BattlefieldOption[]> {
  return battlefields()
}
