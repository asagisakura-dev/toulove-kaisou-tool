import { ReactNode } from 'react'
import CloseIcon from '@mui/icons-material/Close'
import Chip from '@mui/material/Chip'
import CircularProgress from '@mui/material/CircularProgress'
import Dialog from '@mui/material/Dialog'
import DialogContent from '@mui/material/DialogContent'
import DialogTitle from '@mui/material/DialogTitle'
import Divider from '@mui/material/Divider'
import IconButton from '@mui/material/IconButton'
import Stack from '@mui/material/Stack'
import Typography from '@mui/material/Typography'
import { RecollectionDetail } from '../types'

interface RecollectionDetailModalProps {
  open: boolean
  loading: boolean
  detail: RecollectionDetail | null
  onClose: () => void
}

function DetailRow({ label, children }: { label: string; children: ReactNode }) {
  return (
    <Stack direction="row" spacing={1} alignItems="flex-start">
      <Typography variant="body2" color="text.secondary" sx={{ minWidth: 96, flexShrink: 0 }}>
        {label}
      </Typography>
      <div>{children}</div>
    </Stack>
  )
}

export default function RecollectionDetailModal({
  open,
  loading,
  detail,
  onClose,
}: RecollectionDetailModalProps) {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        {detail ? `回想${detail.number}` : '回想詳細'}
        <IconButton onClick={onClose} size="small" aria-label="閉じる">
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers>
        {loading && (
          <Stack alignItems="center" sx={{ py: 4 }}>
            <CircularProgress size={28} />
          </Stack>
        )}

        {!loading && detail && (
          <Stack spacing={2}>
            <Typography variant="h6" sx={{ wordBreak: 'break-word' }}>
              {detail.title}
            </Typography>

            <DetailRow label="合戦場">
              <Typography variant="body1">
                {detail.battlefieldName
                  ? detail.battlefieldEra
                    ? `${detail.battlefieldName}(${detail.battlefieldEra})`
                    : detail.battlefieldName
                  : '-'}
              </Typography>
            </DetailRow>

            <DetailRow label="必要刀剣男士">
              <Stack direction="row" spacing={0.5} flexWrap="wrap" useFlexGap sx={{ rowGap: 0.5 }}>
                {detail.swordNames.length > 0 ? (
                  detail.swordNames.map((name) => (
                    <Chip key={name} label={name} size="small" color="primary" variant="outlined" />
                  ))
                ) : (
                  <Typography variant="body1">-</Typography>
                )}
              </Stack>
            </DetailRow>

            <DetailRow label="発生条件">
              <Stack spacing={0.5} alignItems="flex-start">
                {detail.bossRequired && (
                  <Chip label="ボス関連条件あり" size="small" color="warning" variant="outlined" />
                )}
                <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
                  {detail.condition ?? '-'}
                </Typography>
              </Stack>
            </DetailRow>

            <Divider />

            <DetailRow label="前提回想">
              <Typography variant="body1">{detail.prerequisiteTitle ?? 'なし'}</Typography>
            </DetailRow>

            <DetailRow label="備考">
              <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
                {detail.remarks || '-'}
              </Typography>
            </DetailRow>
          </Stack>
        )}
      </DialogContent>
    </Dialog>
  )
}
