import { useEffect, useMemo, useState } from 'react'
import Alert from '@mui/material/Alert'
import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import CircularProgress from '@mui/material/CircularProgress'
import Container from '@mui/material/Container'
import Stack from '@mui/material/Stack'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'

import SearchArea from './components/SearchArea'
import RecollectionList from './components/RecollectionList'
import RecollectionDetailModal from './components/RecollectionDetailModal'
import { BattlefieldOption, RecollectionDetail, RecollectionListItem, SwordOption } from './types'
import {
  fetchBattlefieldOptions,
  fetchRecollectionDetail,
  fetchRecollections,
  fetchSwordOptions,
} from './api/recollectionApi'

export default function App() {
  // プルダウンの選択肢
  const [swordOptions, setSwordOptions] = useState<SwordOption[]>([])
  const [battlefieldOptions, setBattlefieldOptions] = useState<BattlefieldOption[]>([])

  // 検索条件
  const [selectedSword, setSelectedSword] = useState('')
  const [selectedSchool, setSelectedSchool] = useState('')
  const [selectedType, setSelectedType] = useState('')
  const [selectedBattlefield, setSelectedBattlefield] = useState('')

  // 一覧
  const [items, setItems] = useState<RecollectionListItem[]>([])
  const [listLoading, setListLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // 詳細モーダル
  const [modalOpen, setModalOpen] = useState(false)
  const [detail, setDetail] = useState<RecollectionDetail | null>(null)
  const [detailLoading, setDetailLoading] = useState(false)

  // プルダウンの選択肢を初回読み込み
  useEffect(() => {
    fetchSwordOptions()
      .then(setSwordOptions)
      .catch(() => setSwordOptions([]))
    fetchBattlefieldOptions()
      .then(setBattlefieldOptions)
      .catch(() => setBattlefieldOptions([]))
  }, [])

  // 検索条件が変わるたびに一覧を再取得
  useEffect(() => {
    let cancelled = false
    setListLoading(true)
    setError(null)

    fetchRecollections(selectedSword || undefined, selectedBattlefield || undefined)
      .then((data) => {
        if (!cancelled) setItems(data)
      })
      .catch(() => {
        if (!cancelled) {
          setError('回想一覧の取得に失敗しました。バックエンドが起動しているか確認してください。')
        }
      })
      .finally(() => {
        if (!cancelled) setListLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [selectedSword, selectedBattlefield])

  const handleSelect = (id: number) => {
    setModalOpen(true)
    setDetailLoading(true)
    setDetail(null)

    fetchRecollectionDetail(id)
      .then(setDetail)
      .catch(() => setError('回想詳細の取得に失敗しました。'))
      .finally(() => setDetailLoading(false))
  }

  const handleSwordSearch = (name: string) => {
    setSelectedSchool('')
    setSelectedType('')
    setSelectedSword(name)
  }

  const resultCountLabel = useMemo(() => `${items.length}件`, [items])

  return (
    <Box sx={{ minHeight: '100vh', backgroundColor: '#ffffff' }}>
      <AppBar position="static" color="default" elevation={1} sx={{ backgroundColor: '#ffffff' }}>
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ color: '#333' }}>
            刀剣乱舞 回想回収支援ツール
          </Typography>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ py: 3 }}>
        <SearchArea
          swordOptions={swordOptions}
          battlefieldOptions={battlefieldOptions}
          selectedSword={selectedSword}
          selectedSchool={selectedSchool}
          selectedType={selectedType}
          selectedBattlefield={selectedBattlefield}
          onSwordChange={setSelectedSword}
          onSchoolChange={setSelectedSchool}
          onTypeChange={setSelectedType}
          onBattlefieldChange={setSelectedBattlefield}
        />

        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          {resultCountLabel}
        </Typography>

        {listLoading ? (
          <Stack alignItems="center" sx={{ py: 6 }}>
            <CircularProgress />
          </Stack>
        ) : (
          <RecollectionList
            items={items}
            onSelect={handleSelect}
            onSwordClick={handleSwordSearch}
          />
        )}
      </Container>

      <RecollectionDetailModal
        open={modalOpen}
        loading={detailLoading}
        detail={detail}
        onClose={() => setModalOpen(false)}
      />
    </Box>
  )
}
