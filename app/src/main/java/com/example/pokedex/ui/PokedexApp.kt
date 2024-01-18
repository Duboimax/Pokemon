@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.pokedex.ui


import PokemonDetails
import PokemonDetailsScreen
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.R
import com.example.pokedex.model.Pokemon
import com.example.pokedex.ui.screens.HomeScreen
import com.example.pokedex.ui.screens.PokedexUiState
import com.example.pokedex.ui.screens.PokedexViewModel

enum class PokemonScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    PokemonDetails(title = R.string.pokemon_details)
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun PokedexApp(
    navController: NavHostController = rememberNavController()
) {
    var currentPokemon: Pokemon? by remember { mutableStateOf(null) }
    val pokemonViewModel: PokedexViewModel = viewModel(factory = PokedexViewModel.Factory)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val canNavigateBack = backStackEntry?.destination?.route != PokemonScreen.Start.name
    val scrollState = rememberLazyGridState()

    Box {
        NavHost(
            navController = navController,
            startDestination = PokemonScreen.Start.name,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = PokemonScreen.Start.name) {
                HomeScreen(
                    pokedexUiState = pokemonViewModel.pokedexUiState,
                    retryAction = pokemonViewModel::getPokemon,
                    viewModel = pokemonViewModel,
                    scrollState = scrollState,
                    onPokedexClicked = { pokemon ->
                        currentPokemon = pokemon
                        navController.navigate(PokemonScreen.PokemonDetails.name)
                    }
                )
            }
            composable(route = PokemonScreen.PokemonDetails.name) {
                PokemonDetailsScreen(
                    pokemon = currentPokemon!!,
                    pokedexUiState = pokemonViewModel.pokedexUiState,
                    retryAction = pokemonViewModel::getPokemon,
                    navController = navController,
                    viewModel = pokemonViewModel,
                    modifier = Modifier.fillMaxSize()
                    .padding(top = 100.dp)
                )
            }
            composable("pokemonDetails/{pokemonId}") { backStackEntry ->
                val pokemonId = backStackEntry.arguments!!.getString("pokemonId")?.toIntOrNull() ?: 0
                val pokemon: Pokemon = pokemonViewModel.getPokemonById(pokemonId)
                    PokemonDetailsScreen(
                        pokemon = pokemon,
                        pokedexUiState = PokedexUiState.Succes(listOf(pokemon)), // Vous pouvez passer une liste avec un seul Pokémon
                        retryAction = { /* Gérez la tentative de rechargement ici si nécessaire */ },
                        navController = navController,
                        viewModel = pokemonViewModel
                    )
            }

        }
        PokedexTopAppBar(
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexTopAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Retour"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFD32F2F),
            titleContentColor = Color.White
        ),
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    )
}
