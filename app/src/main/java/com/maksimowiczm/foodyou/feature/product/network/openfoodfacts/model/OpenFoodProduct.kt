package com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.model

interface OpenFoodProduct {
    val productName: String
    val brands: String?
    val code: String?
    val imageUrl: String?
    val nutriments: OpenFoodNutriments
    val packageQuantity: Float?
    val packageQuantityUnit: String?
    val servingQuantity: Float?
    val servingQuantityUnit: String?
}
