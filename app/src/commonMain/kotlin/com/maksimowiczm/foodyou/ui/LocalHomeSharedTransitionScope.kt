package com.maksimowiczm.foodyou.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalHomeSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
