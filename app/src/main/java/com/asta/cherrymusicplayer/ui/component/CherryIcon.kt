package com.asta.cherrymusicplayer.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.asta.cherrymusicplayer.R

@Composable
fun CherryIcon(filled: Boolean, modifier: Modifier = Modifier, size: Dp = 24.dp,
    contentDescription: String? = if (filled) "Remove from favorites" else "Add to favorites") {
    Icon(painterResource(if (filled) R.drawable.cherry_mark else R.drawable.cherry_outline),
        contentDescription = contentDescription,
        tint = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f),
        modifier = modifier.size(size))
}

@Composable
fun SleepyCherryIcon(isActive: Boolean, modifier: Modifier = Modifier, size: Dp = 26.dp) {
    Icon(Icons.Rounded.Bedtime, contentDescription = if (isActive) "Sleep timer active" else "Sleep timer",
        tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(size))
}
