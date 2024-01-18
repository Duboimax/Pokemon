package com.example.pokedex.data

import com.example.pokedex.model.Pokemon
import com.example.pokedex.services.PokedexApiService

/**
 * Repository that fetch pokemon list from pokemonApi.
 */
interface PokedexRepository {
    /** Fetches list of PokemonPhoto from pokemonApi */
    suspend fun getPokemons(): List<Pokemon>

    suspend fun getPokemonDetailsById(id: Int): Pokemon
}
/**
 * Network Implementation of Repository that fetch pokemon list from pokemonApi.
 */
class NetworkPokedexRepository(
    private val pokemonService: PokedexApiService
) : PokedexRepository {
    /** Fetches list of Pokemon from marsApi*/
    override suspend fun getPokemons(): List<Pokemon> = pokemonService.getPokemons()

    override suspend fun getPokemonDetailsById(id: Int): Pokemon {
        return pokemonService.getPokemonById(id)
    }
}
