import FormControl from '@mui/material/FormControl'
import InputLabel from '@mui/material/InputLabel'
import ListSubheader from '@mui/material/ListSubheader'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Select, { SelectChangeEvent } from '@mui/material/Select'
import Stack from '@mui/material/Stack'
import { BattlefieldOption, SwordOption } from '../types'

interface SearchAreaProps {
  swordOptions: SwordOption[]
  battlefieldOptions: BattlefieldOption[]
  selectedSword: string
  selectedSchool: string
  selectedType: string
  selectedBattlefield: string
  onSwordChange: (value: string) => void
  onSchoolChange: (value: string) => void
  onTypeChange: (value: string) => void
  onBattlefieldChange: (value: string) => void
}

/** 合戦場の表示ラベルを組み立てる。name が重複する場合(例:鳥羽)は時代を付記して区別する。 */
function buildBattlefieldLabel(option: BattlefieldOption, isDuplicateName: boolean): string {
  const prefix = option.mapNumber ? `${option.mapNumber} ` : ''
  const suffix = isDuplicateName && option.era ? `(${option.era})` : ''
  return `${prefix}${option.name}${suffix}`
}

export default function SearchArea({
  swordOptions,
  battlefieldOptions,
  selectedSword,
  selectedSchool,
  selectedType,
  selectedBattlefield,
  onSwordChange,
  onSchoolChange,
  onTypeChange,
  onBattlefieldChange,
}: SearchAreaProps) {
  const handleSwordChange = (event: SelectChangeEvent) => {
    onSwordChange(event.target.value)
  }

  const handleBattlefieldChange = (event: SelectChangeEvent) => {
    onBattlefieldChange(event.target.value)
  }

  const filteredSwordOptions = swordOptions.filter((sword) =>
    (!selectedSchool || sword.school === selectedSchool)
    && (!selectedType || sword.type === selectedType),
  )
  const selectedSwordOption = swordOptions.find((sword) => sword.name === selectedSword)
  const swordOptionsForSelect = selectedSwordOption && !filteredSwordOptions.includes(selectedSwordOption)
    ? [selectedSwordOption, ...filteredSwordOptions]
    : filteredSwordOptions

  const collator = new Intl.Collator('ja')
  const schoolCounts = new Map<string, number>()
  const typeCounts = new Map<string, number>()
  for (const sword of swordOptions) {
    if (sword.school) schoolCounts.set(sword.school, (schoolCounts.get(sword.school) ?? 0) + 1)
    if (sword.type) typeCounts.set(sword.type, (typeCounts.get(sword.type) ?? 0) + 1)
  }
  const schools = [...schoolCounts.keys()].sort(collator.compare)
  const types = [...typeCounts.keys()].sort(collator.compare)

  // name が重複する合戦場(例:鳥羽)を判定するための集計
  const nameCounts = new Map<string, number>()
  for (const b of battlefieldOptions) {
    nameCounts.set(b.name, (nameCounts.get(b.name) ?? 0) + 1)
  }

  // バックエンドから sortOrder(ゲーム内順)でソート済みで返ってくる前提で、
  // era が変わるタイミングごとに見出し(ListSubheader)を挟んで表示する
  let previousEra: string | null | undefined = undefined

  return (
    <Paper
      variant="outlined"
      sx={{ p: 2, mb: 3, backgroundColor: '#fafafa' }}
    >
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} flexWrap="wrap" useFlexGap>
        <FormControl fullWidth size="small" sx={{ maxWidth: { sm: 180 } }}>
          <InputLabel id="school-select-label">刀派</InputLabel>
          <Select labelId="school-select-label" value={selectedSchool} label="刀派" onChange={(event) => onSchoolChange(event.target.value)}>
            <MenuItem value=""><em>すべて</em></MenuItem>
            {schools.map((school) => (
              <MenuItem key={school} value={school}>{`${school}（${schoolCounts.get(school)}）`}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small" sx={{ maxWidth: { sm: 180 } }}>
          <InputLabel id="type-select-label">刀種</InputLabel>
          <Select labelId="type-select-label" value={selectedType} label="刀種" onChange={(event) => onTypeChange(event.target.value)}>
            <MenuItem value=""><em>すべて</em></MenuItem>
            {types.map((type) => (
              <MenuItem key={type} value={type}>{`${type}（${typeCounts.get(type)}）`}</MenuItem>
            ))}
          </Select>
        </FormControl>
        <FormControl fullWidth size="small" sx={{ maxWidth: { sm: 280 } }}>
          <InputLabel id="sword-select-label">刀剣男士</InputLabel>
          <Select
            labelId="sword-select-label"
            value={selectedSword}
            label="刀剣男士"
            onChange={handleSwordChange}
            MenuProps={{ PaperProps: { sx: { maxHeight: 420 } } }}
          >
            <MenuItem value="">
              <em>(絞り込みなし)</em>
            </MenuItem>
            {swordOptionsForSelect.map((sword) => (
              <MenuItem key={sword.name} value={sword.name}>
                {sword.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small" sx={{ maxWidth: { sm: 280 } }}>
          <InputLabel id="battlefield-select-label">合戦場</InputLabel>
          <Select
            labelId="battlefield-select-label"
            value={selectedBattlefield}
            label="合戦場"
            onChange={handleBattlefieldChange}
            MenuProps={{ PaperProps: { sx: { maxHeight: 420 } } }}
          >
            <MenuItem value="">
              <em>(絞り込みなし)</em>
            </MenuItem>
            {battlefieldOptions.flatMap((option) => {
              const items = []
              if (option.era !== previousEra) {
                items.push(
                  <ListSubheader key={`era-${option.era ?? 'none'}`}>
                    {option.era || '(時代不明)'}
                  </ListSubheader>,
                )
                previousEra = option.era
              }
              items.push(
                <MenuItem key={option.id} value={option.id}>
                  {buildBattlefieldLabel(option, (nameCounts.get(option.name) ?? 0) > 1)}
                </MenuItem>,
              )
              return items
            })}
          </Select>
        </FormControl>
      </Stack>
    </Paper>
  )
}
