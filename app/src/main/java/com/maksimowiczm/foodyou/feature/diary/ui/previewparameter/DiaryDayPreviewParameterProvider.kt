package com.maksimowiczm.foodyou.feature.diary.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.data.model.Portion
import com.maksimowiczm.foodyou.feature.diary.data.model.defaultGoals
import com.maksimowiczm.foodyou.feature.product.data.model.Product
import com.maksimowiczm.foodyou.feature.product.data.model.ProductSource
import com.maksimowiczm.foodyou.feature.product.data.model.WeightUnit
import java.time.LocalDate
import java.util.Calendar

class DiaryDayPreviewParameterProvider : PreviewParameterProvider<DiaryDay> {
    override val values: Sequence<DiaryDay> = sequenceOf(
        DiaryDay(
            date = LocalDate.of(2024, Calendar.DECEMBER, 8),
            dailyGoals = defaultGoals(),
            productPotions = mapOf(
                Meal.Breakfast to listOf(
                    Portion(
                        product = Product(
                            id = 0,
                            name = "Egg",
                            brand = "Chicken land",
                            barcode = "1234567890123",
                            calories = 155f,
                            proteins = 13f,
                            carbohydrates = 1.1f,
                            sugars = 1.1f,
                            fats = 11f,
                            saturatedFats = 3.3f,
                            salt = 0.5f,
                            sodium = 0.2f,
                            fiber = 0f,
                            packageWeight = 60f,
                            servingWeight = 60f,
                            weightUnit = WeightUnit.Gram,
                            productSource = ProductSource.User
                        ),
                        weightMeasurement = WeightMeasurement.WeightUnit(60f)
                    ),
                    Portion(
                        product = Product(
                            id = 1,
                            name = "Bread",
                            calories = 265f,
                            proteins = 9f,
                            carbohydrates = 49f,
                            sugars = 1.2f,
                            fats = 2.7f,
                            saturatedFats = 0.5f,
                            salt = 1.1f,
                            sodium = 0.4f,
                            fiber = 3.5f,
                            weightUnit = WeightUnit.Gram,
                            productSource = ProductSource.User
                        ),
                        weightMeasurement = WeightMeasurement.WeightUnit(130f)
                    ),
                    Portion(
                        product = Product(
                            id = 2,
                            name = "Butter",
                            calories = 717f,
                            proteins = 0.9f,
                            carbohydrates = 0.1f,
                            sugars = 0.1f,
                            fats = 81f,
                            saturatedFats = 51f,
                            salt = 0.6f,
                            sodium = 0.2f,
                            fiber = 0f,
                            weightUnit = WeightUnit.Gram,
                            productSource = ProductSource.User
                        ),
                        weightMeasurement = WeightMeasurement.WeightUnit(10f)
                    )
                ),
                Meal.Lunch to listOf(
                    Portion(
                        product = Product(
                            id = 3,
                            name = "Chicken",
                            brand = "Chicken land",
                            calories = 239f,
                            proteins = 27f,
                            carbohydrates = 0f,
                            sugars = 0f,
                            fats = 14f,
                            saturatedFats = 4.1f,
                            salt = 0.1f,
                            sodium = 0.04f,
                            packageWeight = 600f,
                            servingWeight = 100f,
                            fiber = null,
                            weightUnit = WeightUnit.Gram,
                            productSource = ProductSource.User
                        ),
                        weightMeasurement = WeightMeasurement.WeightUnit(150f)
                    ),
                    Portion(
                        product = Product(
                            id = 4,
                            name = "Rice",
                            calories = 130f,
                            proteins = 2.7f,
                            carbohydrates = 28f,
                            sugars = 0.1f,
                            fats = 0.3f,
                            saturatedFats = 0.1f,
                            salt = 0f,
                            sodium = 0.01f,
                            fiber = 0.3f,
                            weightUnit = WeightUnit.Gram,
                            productSource = ProductSource.User
                        ),
                        weightMeasurement = WeightMeasurement.WeightUnit(125f)
                    ),
                    Portion(
                        product = Product(
                            id = 5,
                            name = "Salad",
                            calories = 15f,
                            proteins = 1.3f,
                            carbohydrates = 2.9f,
                            sugars = 1.2f,
                            fats = 0.2f,
                            saturatedFats = 0.1f,
                            salt = 0.1f,
                            sodium = 0.04f,
                            fiber = 1.2f,
                            weightUnit = WeightUnit.Gram,
                            productSource = ProductSource.User
                        ),
                        weightMeasurement = WeightMeasurement.WeightUnit(50f)
                    )
                ),
                Meal.Dinner to listOf(
                    Portion(
                        product = Product(
                            id = 6,
                            name = "Pasta",
                            calories = 131f,
                            proteins = 5.2f,
                            carbohydrates = 25f,
                            sugars = 0.3f,
                            fats = 1.1f,
                            saturatedFats = 0.2f,
                            salt = 0f,
                            sodium = 0.01f,
                            fiber = 1.3f,
                            weightUnit = WeightUnit.Gram,
                            productSource = ProductSource.User
                        ),
                        weightMeasurement = WeightMeasurement.WeightUnit(150f)
                    ),
                    Portion(
                        product = Product(
                            id = 7,
                            name = "Tomato sauce",
                            brand = "Spaghetti Italiano",
                            calories = 82f,
                            proteins = 1.2f,
                            carbohydrates = 16f,
                            sugars = 12f,
                            fats = 1.1f,
                            saturatedFats = 0.2f,
                            salt = 0.1f,
                            sodium = 0.04f,
                            fiber = 1.2f,
                            packageWeight = 500f,
                            servingWeight = 100f,
                            weightUnit = WeightUnit.Gram,
                            productSource = ProductSource.User
                        ),
                        weightMeasurement = WeightMeasurement.WeightUnit(200f)
                    )
                ),
                Meal.Snacks to listOf(
                    Portion(
                        product = Product(
                            id = 8,
                            name = "Cheese",
                            brand = "Sigma Foods",
                            calories = 402f,
                            proteins = 25f,
                            carbohydrates = 0.1f,
                            sugars = 0.1f,
                            fats = 33f,
                            saturatedFats = 21f,
                            salt = 1.6f,
                            sodium = 0.64f,
                            fiber = null,
                            packageWeight = 400f,
                            servingWeight = 30f,
                            weightUnit = WeightUnit.Gram,
                            productSource = ProductSource.User
                        ),
                        weightMeasurement = WeightMeasurement.WeightUnit(30f)
                    ),
                    Portion(
                        product = Product(
                            id = 9,
                            name = "Water",
                            brand = "Rizz waesser",
                            calories = 0f,
                            proteins = 0f,
                            carbohydrates = 0f,
                            fats = 0f,
                            packageWeight = 2000f,
                            servingWeight = 300f,
                            weightUnit = WeightUnit.Millilitre,
                            productSource = ProductSource.User
                        ),
                        weightMeasurement = WeightMeasurement.WeightUnit(500f)
                    )
                )
            )
        )
    )
}
