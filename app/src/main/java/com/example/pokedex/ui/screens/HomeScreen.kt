package com.example.pokedex.ui.screens

import android.text.InputFilter.LengthFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.pokedex.R
import com.example.pokedex.model.Pokemon
import com.example.pokedex.ui.theme.PokedexTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun HomeScreen(
    pokedexUiState: PokedexUiState,
    viewModel: PokedexViewModel,
    retryAction: () -> Unit,
    scrollState: LazyGridState,
    modifier: Modifier = Modifier,
    onPokedexClicked: (Pokemon) -> Unit,
) {
    when (pokedexUiState) {
        is PokedexUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is PokedexUiState.Succes -> PokemonGridScreen(
            pokedexUiState.pokemon,
            modifier = modifier.fillMaxSize(),
            isRefreshing = viewModel.isRefreshing,  // Ajoutez l'état de rafraîchissement
            onRefresh = viewModel::refreshPokemons,
            scrollState = scrollState,
            onPokedexClicked = onPokedexClicked,
            viewModel = viewModel
        )
        is PokedexUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }


}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun PokemonGridScreen(
            pokemons: List<Pokemon>,
            isRefreshing: Boolean,
            onRefresh: () -> Unit,
            modifier: Modifier = Modifier,
            scrollState: LazyGridState,
            onPokedexClicked: (Pokemon) -> Unit,
            viewModel: PokedexViewModel
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh
    ) {

        Column(modifier = modifier.padding(top = 90.dp)) {
            // Titre "Mes Pokemons"
            Text(
                text = stringResource(R.string.title_section),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Divider(
                color = Color(0xFFD32F2F),
                thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp)) // Ajoutez un espace entre le titre et la grille

            LazyVerticalGrid(
                state = scrollState,
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(pokemons) { pokemon ->
                    PokemonListItem(
                        pokemon = pokemon,
                        onItemClick = {
                            onPokedexClicked(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2.5f)
                    )
                }
            }
        }
    }
    if (isRefreshing) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun PokemonListItem(
    pokemon: Pokemon,
    onItemClick: (Pokemon) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = getFirstTypeColor(pokemon.type).copy(alpha = 0.2f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onItemClick(pokemon) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .padding(24.dp) // Appliquer le padding ici
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Affiche l'image du Pokémon
                Image(
                    painter = rememberImagePainter(data = pokemon.image_url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp) // Taille moyenne pour l'image
                        .clip(MaterialTheme.shapes.medium)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    // Affiche le nom et l'ID du Pokémon
                    Text(
                        text = pokemon.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "#${String.format("%03d", pokemon.id)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Affiche les types du Pokémon

                }
                Spacer(Modifier.weight(1f))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Ceci ajoute un espace entre chaque Tag
                ) {
                    pokemon.type.forEach { type ->
                        Tag(type = type, bgColor = getColorForType(type))
                    }
                }
            }
        }
    }
}

@Composable
fun Tag(type: String, bgColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 8.dp) // Adjust padding as needed
    ) {
        TypeIcon(type)
    }
}

@Composable
fun TypeIcon(type: String) {
    val iconId = when (type) {
        "Insecte" -> R.drawable.bug
        "Ténèbres" -> R.drawable.dark
        "Dragon" -> R.drawable.dragon
        "Électrik" -> R.drawable.electric
        "Fée" -> R.drawable.fairy
        "Feu" -> R.drawable.fire
        "Combat" -> R.drawable.fighting
        "Poison" -> R.drawable.poison
        "Normal" -> R.drawable.normal
        "Vol" -> R.drawable.flying
        "Spectre" -> R.drawable.ghost
        "Plante" -> R.drawable.grass
        "Sol" -> R.drawable.ground
        else -> R.drawable.normal // Replace with a generic placeholder if type is unknown
    }
    Image(
        painter = painterResource(id = iconId),
        contentDescription = "Type Icon",
        modifier = Modifier.size(24.dp) // Adjust the size as needed
    )
}

@Composable
fun getFirstTypeColor(types: List<String>): Color {
    val firstType = types.firstOrNull() ?: return Color.LightGray
    return getColorForType(firstType)
}


@Composable
fun getColorForType(type: String): Color {
    return when (type) {
        "Normal" -> Color(0xFFA8A77A)
        "Feu" -> Color(0xFFEE8130)
        "Eau" -> Color(0xFF6390F0)
        "Plante" -> Color(0xFF7AC74C)
        "Insecte" -> Color(0xFFA6B91A)
        "Vol" -> Color(0xFFA98FF3)
        "Poison" -> Color(0xFFA33EA1)
        "Sol" -> Color(0xFFE2BF65)
        "Roche" -> Color(0xFFB6A136)
        "Combat" -> Color(0xFFC22E28)
        "Spectre" -> Color(0xFF735797)
        "Psy" -> Color(0xFFF95587)
        "Electrik" -> Color(0xFFF7D02C)
        "Glace" -> Color(0xFF96D9D6)
        "Dragon" -> Color(0xFF6F35FC)
        else -> MaterialTheme.colorScheme.secondary
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    PokedexTheme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    PokedexTheme {
        ErrorScreen({})
    }
}

