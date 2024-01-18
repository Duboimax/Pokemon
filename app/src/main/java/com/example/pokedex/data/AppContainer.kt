package com.example.pokedex.data

import com.example.pokedex.services.PokedexApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val pokedexRepository: PokedexRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://raw.githubusercontent.com/Josstoh/res508-qualite-dev-android/main/rest/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: PokedexApiService by lazy {
        retrofit.create(PokedexApiService::class.java)
    }

    override val pokedexRepository: PokedexRepository by lazy {
        NetworkPokedexRepository(retrofitService)
    }
}