import Grid from '@mui/material/Grid'
import Typography from '@mui/material/Typography'
import { RecollectionListItem } from '../types'
import RecollectionCard from './RecollectionCard'

interface RecollectionListProps {
  items: RecollectionListItem[]
  onSelect: (id: number) => void
  onSwordClick: (name: string) => void
}

export default function RecollectionList({ items, onSelect, onSwordClick }: RecollectionListProps) {
  if (items.length === 0) {
    return (
      <Typography color="text.secondary" sx={{ mt: 4, textAlign: 'center' }}>
        条件に一致する回想が見つかりませんでした。
      </Typography>
    )
  }

  return (
    <Grid container spacing={2}>
      {items.map((item) => (
        <Grid item xs={12} sm={6} md={4} key={item.id}>
          <RecollectionCard
            item={item}
            onClick={() => onSelect(item.id)}
            onSwordClick={onSwordClick}
          />
        </Grid>
      ))}
    </Grid>
  )
}
