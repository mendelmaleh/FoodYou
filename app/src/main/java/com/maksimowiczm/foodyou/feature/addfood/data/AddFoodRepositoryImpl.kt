package com.maksimowiczm.foodyou.feature.addfood.data

import android.util.Log
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.feature.addfood.data.model.QuantitySuggestion.Companion.defaultSuggestion
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.addfood.data.model.toDomain
import com.maksimowiczm.foodyou.feature.addfood.data.model.toEntity
import com.maksimowiczm.foodyou.feature.addfood.database.AddFoodDao
import com.maksimowiczm.foodyou.feature.addfood.database.AddFoodDatabase
import com.maksimowiczm.foodyou.feature.addfood.database.ProductQueryEntity
import com.maksimowiczm.foodyou.feature.addfood.database.WeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.product.data.model.toDomain
import com.maksimowiczm.foodyou.feature.product.database.ProductDao
import com.maksimowiczm.foodyou.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.feature.product.network.RemoteProductDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.collections.List
import kotlin.collections.associate
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.set
import kotlin.collections.toMutableMap

class AddFoodRepositoryImpl(
    addFoodDatabase: AddFoodDatabase,
    productDatabase: ProductDatabase,
    private val remoteProductDatabase: RemoteProductDatabase,
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : AddFoodRepository {
    private val addFoodDao: AddFoodDao = addFoodDatabase.addFoodDao()
    private val productDao: ProductDao = productDatabase.productDao()

    override suspend fun addFood(
        date: LocalDate,
        meal: Meal,
        productId: Long,
        weightMeasurement: WeightMeasurement
    ): Long {
        val quantity = when (weightMeasurement) {
            is WeightMeasurement.WeightUnit -> weightMeasurement.weight
            is WeightMeasurement.Package -> weightMeasurement.quantity
            is WeightMeasurement.Serving -> weightMeasurement.quantity
        }

        return addFood(
            date = date,
            meal = meal,
            productId = productId,
            weightMeasurement = weightMeasurement.asEnum(),
            quantity = quantity
        )
    }

    override suspend fun addFood(
        date: LocalDate,
        meal: Meal,
        productId: Long,
        weightMeasurement: WeightMeasurementEnum,
        quantity: Float
    ): Long {
        val localZoneOffset = ZoneOffset.systemDefault()
        val currentLocalTime = LocalDateTime.now().atZone(localZoneOffset)
        val epochSeconds = currentLocalTime.toEpochSecond()

        val entity = WeightMeasurementEntity(
            mealId = meal.toEntity(),
            diaryEpochDay = date.toEpochDay(),
            productId = productId,
            measurement = weightMeasurement,
            quantity = quantity,
            createdAt = epochSeconds
        )

        return addFoodDao.insertWeightMeasurement(entity)
    }

    override suspend fun removeFood(portionId: Long) {
        val entity = addFoodDao.observeWeightMeasurement(portionId).first()

        if (entity != null) {
            addFoodDao.deleteWeightMeasurement(entity.id)
        }
    }

    override fun queryProducts(
        meal: Meal,
        date: LocalDate,
        query: String?,
        localOnly: Boolean
    ): Flow<QueryResult<List<ProductWithWeightMeasurement>>> {
        return if (query?.all { it.isDigit() } == true) {
            queryProductsByBarcode(meal, date, query, localOnly)
        } else {
            queryProductsByName(meal, date, query, localOnly)
        }
    }

    private fun queryProductsByBarcode(
        meal: Meal,
        date: LocalDate,
        barcode: String,
        localOnly: Boolean
    ): Flow<QueryResult<List<ProductWithWeightMeasurement>>> = flow {
        val flow = { isLoading: Boolean, error: Throwable? ->
            addFoodDao.observeProductsWithMeasurementByBarcode(
                meal = meal,
                date = date,
                barcode = barcode
            ).map { products ->
                QueryResult(
                    data = products,
                    isLoading = isLoading,
                    error = error
                )
            }
        }

        if (!localOnly) {
            // First get local products and emit loading state
            flow(true, null).first().also { emit(it) }

            try {
                remoteProductDatabase.queryAndInsertByBarcode(barcode)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to query products", e)

                flow(false, e).collect(::emit)
            }
        }

        flow(false, null).collect(::emit)
    }

    private fun queryProductsByName(
        meal: Meal,
        date: LocalDate,
        query: String?,
        localOnly: Boolean
    ): Flow<QueryResult<List<ProductWithWeightMeasurement>>> = flow {
        // Insert the query to the history
        if (query != null) {
            ioScope.launch {
                insertProductQueryWithCurrentTime(query)
            }
        }

        val flow = { isLoading: Boolean, error: Throwable? ->
            addFoodDao.observeProductsWithMeasurementByQuery(
                meal = meal,
                date = date,
                query = query
            ).map { products ->
                QueryResult(
                    data = products,
                    isLoading = isLoading,
                    error = error
                )
            }
        }

        if (!localOnly) {
            // First get local products and emit loading state
            flow(true, null).first().also { emit(it) }

            try {
                remoteProductDatabase.queryAndInsertByName(query)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to query products", e)

                flow(false, e).collect(::emit)
            }
        }

        flow(false, null).collect(::emit)
    }

    private suspend fun insertProductQueryWithCurrentTime(query: String) {
        val zone = ZoneOffset.UTC
        val time = LocalDateTime.now().atZone(zone)
        val epochSeconds = time.toEpochSecond()

        addFoodDao.upsertProductQuery(
            ProductQueryEntity(
                query = query,
                date = epochSeconds
            )
        )
    }

    override fun observeMeasuredProducts(
        meal: Meal,
        date: LocalDate
    ): Flow<List<ProductWithWeightMeasurement>> {
        return addFoodDao.observeMeasuredProducts(
            mealId = meal.toEntity().value,
            epochDay = date.toEpochDay()
        ).map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun getQuantitySuggestionByProductId(productId: Long): QuantitySuggestion {
        val product = productDao.getProductById(productId) ?: error("Product not found")

        val suggestionList = addFoodDao.observeQuantitySuggestionsByProductId(productId).first()

        val suggestions = suggestionList
            .associate { it.measurement to it.quantity }
            .toMutableMap()

        val default = defaultSuggestion()
        WeightMeasurementEnum.entries.forEach {
            if (!suggestions.containsKey(it)) {
                suggestions[it] = default[it] ?: error("Default suggestion not found for $it")
            }
        }

        return QuantitySuggestion(
            product = product.toDomain(),
            quantitySuggestions = suggestions
        )
    }

    override fun observeProductQueries(limit: Int): Flow<List<ProductQuery>> {
        return addFoodDao.observeLatestQueries(limit).map { list ->
            list.map { it.toDomain() }
        }
    }

    private fun AddFoodDao.observeProductsWithMeasurementByQuery(
        meal: Meal,
        date: LocalDate,
        query: String?
    ): Flow<List<ProductWithWeightMeasurement>> = observeProductsWithMeasurement(
        mealId = meal.toEntity().value,
        epochDay = date.toEpochDay(),
        query = query,
        barcode = null,
        limit = PAGE_SIZE
    ).map { list -> list.map { it.toDomain() } }

    private fun AddFoodDao.observeProductsWithMeasurementByBarcode(
        meal: Meal,
        date: LocalDate,
        barcode: String
    ): Flow<List<ProductWithWeightMeasurement>> = observeProductsWithMeasurement(
        mealId = meal.toEntity().value,
        epochDay = date.toEpochDay(),
        query = null,
        barcode = barcode,
        limit = PAGE_SIZE
    ).map { list -> list.map { it.toDomain() } }

    private companion object {
        private const val TAG = "AddFoodRepositoryImpl"
        private const val PAGE_SIZE = 30
    }
}
