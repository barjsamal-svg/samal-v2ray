package com.samal.v2ray.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samal.v2ray.R
import com.samal.v2ray.ui.compose.colorFabActive
import com.samal.v2ray.ui.compose.colorFabInactiveDark
import com.samal.v2ray.ui.compose.colorFabInactiveLight

@Composable
fun MainBottomBar(
    displayText: String,
    isRunning: Boolean,
    isDarkTheme: Boolean,
    onAction: (MainAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Glassmorphism Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp)),
            color = if (isDarkTheme) Color(0xCC1A1A1A) else Color(0xCCF5F5F5),
            tonalElevation = 8.dp,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { onAction(MainAction.TestCurrentServer) })
                ) {
                    Text(
                        text = if (isRunning) "CONNECTED" else "DISCONNECTED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = if (isRunning) colorFabActive else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Connection Toggle Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = if (isRunning) 
                                    listOf(colorFabActive, colorFabActive.copy(alpha = 0.7f))
                                else 
                                    listOf(Color.Gray, Color.DarkGray)
                            )
                        )
                        .clickable { onAction(MainAction.ToggleService) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = if (isRunning) painterResource(R.drawable.ic_stop_24dp)
                        else painterResource(R.drawable.ic_play_24dp),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
