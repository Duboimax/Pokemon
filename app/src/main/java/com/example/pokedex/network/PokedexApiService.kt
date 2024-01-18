/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.pokedex.services

import com.example.pokedex.model.Pokemon
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * A public interface that exposes the [getPokemons] method
 */
interface PokedexApiService {
    /**
     * Returns a [List] of [Pokemon] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("pokemons.json")
    suspend fun getPokemons(): List<Pokemon>

    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int): Pokemon
}
