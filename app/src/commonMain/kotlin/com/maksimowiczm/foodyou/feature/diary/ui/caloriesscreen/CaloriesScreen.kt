package com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.data.model.Product
import com.maksimowiczm.foodyou.feature.diary.ui.CaloriesIndicatorTransitionKeys
import com.maksimowiczm.foodyou.feature.diary.ui.component.CaloriesIndicator
import com.maksimowiczm.foodyou.feature.diary.ui.component.MealsFilter
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientsList
import com.maksimowiczm.foodyou.feature.diary.ui.component.rememberMealsFilterState
import com.maksimowiczm.foodyou.ui.LocalHomeSharedTransitionScope
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.description_incomplete_nutrition_data
import foodyou.app.generated.resources.headline_incomplete_products
import foodyou.app.generated.resources.unit_gram_short
import kotlin.math.roundToInt
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CaloriesScreen(
    date: LocalDate,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CaloriesScreenViewModel = koinViewModel()
) {
    val diaryDay by viewModel.observeDiaryDay(date).collectAsStateWithLifecycle(null)

    if (diaryDay != null) {
        CaloriesScreen(
            diaryDay = diaryDay!!,
            formatDate = viewModel::formatDate,
            animatedVisibilityScope = animatedVisibilityScope,
            onProductClick = onProductClick,
            modifier = modifier
        )
    } else {
        Surface(modifier) {
            Spacer(Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun CaloriesScreen(
    diaryDay: DiaryDay,
    formatDate: (LocalDate) -> String,
    onProductClick: (Product) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val filterState = rememberMealsFilterState(diaryDay.meals.toSet())
    val meals by remember(filterState.selectedMeals) {
        derivedStateOf {
            if (filterState.selectedMeals.isEmpty()) {
                diaryDay.meals
            } else {
                diaryDay.meals.filter { it.id in filterState.selectedMeals }
            }
        }
    }

    val homeSTS = LocalHomeSharedTransitionScope.current ?: error("No SharedTransitionScope")

    val topBar = @Composable {
        val insets = TopAppBarDefaults.windowInsets

        with(homeSTS) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(insets.asPaddingValues())
                        .consumeWindowInsets(insets)
                        .padding(16.dp)
                ) {
                    with(animatedVisibilityScope) {
                        Text(
                            text = formatDate(diaryDay.date),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .animateEnterExit(
                                    enter = fadeIn(
                                        tween(
                                            delayMillis = DefaultDurationMillis
                                        )
                                    ),
                                    exit = fadeOut(tween(50))
                                )
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    CaloriesIndicator(
                        calories = diaryDay.totalCalories.roundToInt(),
                        caloriesGoal = diaryDay.dailyGoals.calories,
                        proteins = diaryDay.totalProteins.roundToInt(),
                        carbohydrates = diaryDay.totalCarbohydrates.roundToInt(),
                        fats = diaryDay.totalFats.roundToInt(),
                        modifier = Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = CaloriesIndicatorTransitionKeys.CaloriesIndicator(
                                    epochDay = diaryDay.date.toEpochDays()
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = topBar,
        modifier = modifier
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleMedium
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                item {
                    MealsFilter(
                        state = filterState,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    NutrientsList(
                        products = diaryDay.meals
                            .filter { it in meals }
                            .flatMap { diaryDay.mealProductMap[it] ?: emptyList() },
                        incompleteValue = {
                            {
                                val g = stringResource(Res.string.unit_gram_short)
                                val value = it.formatClipZeros()
                                Text(
                                    text = "* $value $g",
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    val products = diaryDay.meals
                        .filter { it in meals }
                        .flatMap { diaryDay.mealProductMap[it] ?: emptyList() }

                    val anyProductIncomplete = products
                        .any { !it.product.nutrients.isComplete }

                    // Display incomplete products
                    if (anyProductIncomplete) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text =
                                "*" +
                                    stringResource(
                                        Res.string.description_incomplete_nutrition_data
                                    ),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.outline
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = stringResource(Res.string.headline_incomplete_products),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )

                            val products = products
                                .map { it.product }
                                .distinct()
                                .filter { !it.nutrients.isComplete }

                            products.forEach { product ->
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                    textAlign = TextAlign.Companion.Center,
                                    modifier = Modifier.clickable(
                                        interactionSource = remember {
                                            MutableInteractionSource()
                                        },
                                        indication = null,
                                        onClick = {
                                            onProductClick(product)
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
