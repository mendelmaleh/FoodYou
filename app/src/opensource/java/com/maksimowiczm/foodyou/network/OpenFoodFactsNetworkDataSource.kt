package com.maksimowiczm.foodyou.network

import android.util.Log
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.network.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.network.model.OpenFoodPageResponse
import com.maksimowiczm.foodyou.network.model.v1.OpenFoodFactsPageResponseV1
import com.maksimowiczm.foodyou.network.model.v2.OpenFoodFactsProductResponseV2
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class OpenFoodFactsNetworkDataSource {
    private val client = HttpClient(OkHttp) {
        defaultRequest {
            url(BuildConfig.OPEN_FOOD_FACTS_URL)
            userAgent(BuildConfig.OPEN_FOOD_FACTS_USER_AGENT)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    private val networkApi = RetrofitOpenFoodFactsNetworkApi(client)

    suspend fun getProduct(code: String, country: String): OpenFoodFactsProduct? {
        val response = networkApi.getProduct(
            code = code,
            countries = country.lowercase()
        )

        if (response.status == HttpStatusCode.NotFound) {
            Log.d(TAG, "Product not found for code: $code")
            return null
        }

        return response.body<OpenFoodFactsProductResponseV2>().product
    }

    suspend fun queryProducts(
        query: String,
        country: String,
        page: Int?,
        pageSize: Int
    ): OpenFoodPageResponse = networkApi.queryProducts(
        query = query,
        countries = country.lowercase(),
        page = page,
        pageSize = pageSize
    ).body<OpenFoodFactsPageResponseV1>()

    private companion object {
        private const val TAG = "OpenFoodFactsNetworkDataSource"
        private const val TIMEOUT = 10_000L
    }
}

private const val FIELDS = "" +
    "product_name" +
    ",code" +
    ",nutriments" +
    ",brands" +
    ",serving_quantity" +
    ",serving_quantity_unit" +
    ",product_quantity" +
    ",product_quantity_unit"

private class RetrofitOpenFoodFactsNetworkApi(private val client: HttpClient) {
    suspend fun getProduct(code: String, countries: String): HttpResponse =
        client.get("api/v2/product/$code") {
            parameter("countries", countries)
            parameter("fields", FIELDS)
        }

    suspend fun queryProducts(
        query: String,
        countries: String,
        page: Int?,
        pageSize: Int
    ): HttpResponse = client.get("cgi/search.pl?search_simple=1&json=1") {
        parameter("search_terms", query)
        parameter("countries", countries)
        parameter("page", page)
        parameter("page_size", pageSize)
        parameter("fields", FIELDS)
    }
}
