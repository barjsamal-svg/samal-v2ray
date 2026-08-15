@Composable
fun ServerListItem(
    remarks: String,
    statistics: String,
    typeDescription: String,
    testResult: String,
    testDelayMillis: Long,
    isSelected: Boolean,
    subscriptionRemarks: String,
    doubleColumnDisplay: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onRemove: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
    dragModifier: Modifier = Modifier
) {
    androidx.compose.material3.ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .then(dragModifier),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = androidx.compose.material3.CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        onClick = onClick
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Protocol Icon/Indicator
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = typeDescription.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                else MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = remarks,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = typeDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                else MaterialTheme.colorScheme.primary
                    )
                }

                if (doubleColumnDisplay) {
                    IconButton(onClick = onMore) {
                        Icon(painterResource(R.drawable.ic_more_vert_24dp), null)
                    }
                } else {
                    Row {
                        IconButton(onClick = onShare, Modifier.size(32.dp)) { 
                            Icon(painterResource(R.drawable.ic_share_24dp), null, Modifier.size(20.dp)) 
                        }
                        IconButton(onClick = onEdit, Modifier.size(32.dp)) { 
                            Icon(painterResource(R.drawable.ic_edit_24dp), null, Modifier.size(20.dp)) 
                        }
                        IconButton(onClick = onRemove, Modifier.size(32.dp)) { 
                            Icon(painterResource(R.drawable.ic_delete_24dp), null, Modifier.size(20.dp)) 
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_image_24dp), // Placeholder for info icon
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = statistics,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Text(
                    text = testResult,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (testDelayMillis < 0L) colorPingRed else colorPing
                )
            }
        }
    }
}
