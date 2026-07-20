import Card from '@mui/material/Card'
import CardActionArea from '@mui/material/CardActionArea'
import CardContent from '@mui/material/CardContent'
import Chip from '@mui/material/Chip'
import Stack from '@mui/material/Stack'
import Typography from '@mui/material/Typography'
import { RecollectionListItem } from '../types'

interface RecollectionCardProps {
  item: RecollectionListItem
  onClick: () => void
  onSwordClick: (name: string) => void
}

export default function RecollectionCard({ item, onClick, onSwordClick }: RecollectionCardProps) {
  return (
    <Card variant="outlined" sx={{ height: '100%' }}>
      <CardActionArea onClick={onClick} sx={{ height: '100%', alignItems: 'stretch' }}>
        <CardContent>
          <Typography variant="subtitle2" color="text.secondary">
            回想{item.number}
          </Typography>

          <Typography variant="h6" component="div" sx={{ mb: 1, wordBreak: 'break-word' }}>
            {item.title}
          </Typography>

          {(item.battlefieldName || item.bossRequired) && (
            <Stack direction="row" spacing={0.5} sx={{ mb: 1.5 }}>
              {item.battlefieldName && (
                <Chip
                  label={
                    item.battlefieldEra
                      ? `${item.battlefieldName}(${item.battlefieldEra})`
                      : item.battlefieldName
                  }
                  size="small"
                />
              )}
              {item.bossRequired && (
                <Chip label="ボス関連条件あり" size="small" color="warning" variant="outlined" />
              )}
            </Stack>
          )}

          <Typography variant="body2" color="text.secondary">
            必要刀剣
          </Typography>
          <Stack direction="row" spacing={0.5} flexWrap="wrap" useFlexGap sx={{ mb: 1.5, rowGap: 0.5 }}>
            {item.swordNames.length > 0 ? (
              item.swordNames.map((name) => (
                <Chip
                  key={name}
                  label={name}
                  size="small"
                  color="primary"
                  variant="outlined"
                  clickable
                  onClick={(event) => {
                    event.stopPropagation()
                    onSwordClick(name)
                  }}
                />
              ))
            ) : (
              <Typography variant="body2">-</Typography>
            )}
          </Stack>

          {item.condition && (
            <>
              <Typography variant="body2" color="text.secondary">
                条件
              </Typography>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                {item.condition}
              </Typography>
            </>
          )}
        </CardContent>
      </CardActionArea>
    </Card>
  )
}
