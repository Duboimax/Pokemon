package com.example.pokedex.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pokedex.PokedexApplication
import com.example.pokedex.data.PokedexRepository
import com.example.pokedex.model.Pokemon
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface PokedexUiState {
    data class Succes(val pokemon: List<Pokemon>) : PokedexUiState
    object Error : PokedexUiState
    object Loading : PokedexUiState
}

class PokedexViewModel( private val pokedexRepository: PokedexRepository) : ViewModel() {
    var pokedexUiState: PokedexUiState by mutableStateOf(PokedexUiState.Loading)
        private set

    var pokemonList by mutableStateOf<List<Pokemon>>(emptyList())
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    init {
        getPokemon()
    }

    fun getPokemon() {
        viewModelScope.launch {
            try {
                val pokemons = pokedexRepository.getPokemons()
                pokemonList = pokemons
                pokedexUiState = PokedexUiState.Succes(pokemons)
            } catch (e: IOException) {
                pokedexUiState = PokedexUiState.Error
            } catch (e: HttpException) {
                pokedexUiState = PokedexUiState.Error
            }
        }
    }

    fun getEvolutionsById(pokemonIds: List<Int>): List<Pokemon> {
        val result = pokemonList.filter { it.id in pokemonIds }
        Log.d("Debug", "Result size: ${result.size}")
        return result
    }

    fun getPokemonById(pokemonId: Int): Pokemon {
        val result: Pokemon = pokemonList.first() { it.id == pokemonId }
        return result
    }

    fun refreshPokemons() {
        viewModelScope.launch {
            isRefreshing = true
            try {
                val pokemons = pokedexRepository.getPokemons() ?: emptyList()
                pokemonList = pokemons
                pokedexUiState = PokedexUiState.Succes(pokemons)
            } catch (e: IOException) {
                pokedexUiState = PokedexUiState.Error
            } catch (e: HttpException) {
                pokedexUiState = PokedexUiState.Error
            } finally {
                isRefreshing = false
            }
        }
    }

    fun setTestPokemonList(testPokemons: List<Pokemon>) {
        pokemonList = testPokemons
    }

    companion object{
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PokedexApplication)
                val pokedexRepository = application.container.pokedexRepository
                PokedexViewModel(pokedexRepository = pokedexRepository)
            }
        }
    }
}