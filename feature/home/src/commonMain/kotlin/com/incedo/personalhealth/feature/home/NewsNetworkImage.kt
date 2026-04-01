package com.incedo.personalhealth.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
internal expect fun NewsNetworkImage(
    imageUrl: String,
    contentDescription: String,
    height: Dp,
    modifier: Modifier = Modifier
)
