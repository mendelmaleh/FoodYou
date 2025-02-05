package com.maksimowiczm.foodyou.core.feature.diary.ui.nutrimentscard

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.core.feature.HomeFeature
import com.maksimowiczm.foodyou.core.feature.diary.DiaryFeature.navigateToGoalsSettings
import com.maksimowiczm.foodyou.core.feature.diary.ui.DiaryViewModel
import org.koin.androidx.compose.koinViewModel

fun buildNutrimentsCard(navController: NavController) = HomeFeature(
    applyPadding = false
) { modifier, homeState ->
    val viewModel = koinViewModel<DiaryViewModel>()

    val diaryDay by viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)

    diaryDay?.let {
        NutrimentsRowCard(
            diaryDay = it,
            onSettingsClick = {
                navController.navigateToGoalsSettings(
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            modifier = modifier
        )
    }
}
