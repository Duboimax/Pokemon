import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.pokedex.model.Pokemon
import com.example.pokedex.ui.screens.ErrorScreen
import com.example.pokedex.ui.screens.LoadingScreen
import com.example.pokedex.ui.screens.PokedexUiState
import com.example.pokedex.ui.screens.PokedexViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pokedex.R
import com.example.pokedex.ui.screens.Tag
import com.example.pokedex.ui.screens.getColorForType

@Composable
fun PokemonDetailsScreen(
    pokemon: Pokemon,
    pokedexUiState: PokedexUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PokedexViewModel
) {
    when (pokedexUiState) {
        is PokedexUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is PokedexUiState.Succes ->
            Box(modifier = Modifier.padding(top = 90.dp)) {
                PokemonDetails(
                    pokemon,
                    viewModel,
                    navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            }


        is PokedexUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }
}

@Composable
fun PokemonDetails(
    pokemon: Pokemon,
    viewModel: PokedexViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = getFirstTypeColor(types = pokemon.type)
    Box {
        // Draw the oval background
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {  // Set the height of the oval here
            val path = Path().apply {
                // Define an oval path
                addOval(Rect(-size.width / 2, -100f, size.width * 1.5f, size.height))
            }
            drawPath(path, backgroundColor)
        }

        // Place your Pokémon information here
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(
                text = pokemon.name,
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)

            )
            Text(
                text = String.format("#%03d", pokemon.id),
                color = Color.White,
            )
            Image(
                painter = rememberImagePainter(data = pokemon.image_url),
                contentDescription = "",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Start)
            ) {
                pokemon.type.forEach { type ->
                    val tagColor = getColorForType(type)
                    Tag(type = type, bgColor = tagColor)
                }

            }
            Spacer(modifier = Modifier.height(16.dp))

            // Pokémon Description
            Text(
                text = pokemon.description,
                style = typography.bodyMedium,
                color = colorScheme.onBackground,
                textAlign = TextAlign.Justify,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pokémon Evolutions
            if (pokemon.evolutions.after.isNotEmpty()) {
                Divider(
                    color = backgroundColor,
                    thickness = 2.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.label_evolution),
                    style = typography.titleSmall,
                    color = colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Déterminez ici les évolutions avant et après
                val prevEvolutions = viewModel.getEvolutionsById(pokemon.evolutions.before)
                val nextEvolutions = viewModel.getEvolutionsById(pokemon.evolutions.after)

                // Afficher les évolutions précédentes si aucune évolution suivante n'est présente
                val evolutionsToShow = if (nextEvolutions.isEmpty()) prevEvolutions else nextEvolutions

                // Appel à PokemonEvolutionTimeline en lui passant la liste des évolutions
                PokemonEvolutionTimeline(
                    navController = navController,
                    pokemon = pokemon,
                    prevEvolutions = prevEvolutions,
                    nextEvolutions = evolutionsToShow, // Utilisez evolutionsToShow ici
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun PokemonEvolutionTimeline(
    pokemon: Pokemon,
    navController: NavController,
    prevEvolutions: List<Pokemon>, // Liste des évolutions précédentes
    nextEvolutions: List<Pokemon>, // Liste des évolutions suivantes
    viewModel: PokedexViewModel
) {
    // Déterminer la taille des images en fonction du nombre total d'évolutions affichées
    val imageSize = when {
        prevEvolutions.isNotEmpty() || nextEvolutions.isNotEmpty() -> 60.dp
        else -> 90.dp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Construire la liste des évolutions à afficher
        val evolutionsToShow = mutableListOf<Pokemon>().apply {
            if (nextEvolutions.isEmpty() && prevEvolutions.size >= 2) {
                // Si aucune évolution suivante, afficher les deux dernières évolutions précédentes
                addAll(prevEvolutions.takeLast(2))
            } else {
                // Sinon, afficher la dernière évolution précédente et le Pokémon actuel
                if (prevEvolutions.isNotEmpty()) {
                    add(prevEvolutions.last())
                }
                add(pokemon)
                // Ajouter les évolutions suivantes
                addAll(nextEvolutions)
            }
        }

        // Afficher les Pokémon avec des flèches entre eux
        evolutionsToShow.forEachIndexed { index, evolvedPokemon ->
            EvolutionItem(evolvedPokemon, imageSize = imageSize, isCurrent = evolvedPokemon.id == pokemon.id, navController = navController )

            // Afficher une flèche si ce n'est pas le dernier élément
            if (index < evolutionsToShow.size - 1) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "to",
                    tint = getFirstTypeColor(types = evolvedPokemon.type), // Utilisez la couleur de votre choix
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EvolutionItem(pokemon: Pokemon, imageSize: Dp, isCurrent: Boolean, navController: NavController) {
    val typeColor = getFirstTypeColor(pokemon.type)
    val circleSize = imageSize + 16.dp  // Taille du cercle
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(circleSize).clickable {
            // Naviguer vers l'écran de détails du Pokémon
            navController.navigate("pokemonDetails/${pokemon.id}")
        }) {
            // Dessiner une bordure de cercle si le Pokémon est l'actuel
            if (isCurrent) {
                Canvas(modifier = Modifier.size(circleSize)) {
                    drawCircle(
                        color = typeColor,
                        radius = size.minDimension / 2,
                        center = Offset(size.width / 2, size.height / 2),
                        style = Stroke(width = with(density) { 2.dp.toPx() }) // Définir le style Stroke
                    )
                }
            }
            Image(
                painter = rememberImagePainter(data = pokemon.image_url),
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(imageSize)
                    .align(Alignment.Center)
            )
        }
        // Nom du Pokémon
        Text(
            text = pokemon.name,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun Tag(type: String, bgColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, MaterialTheme.shapes.small)
            .padding(4.dp) // Adjust padding as needed
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
        else -> colorScheme.secondary
    }
}

@Composable
fun getFirstTypeColor(types: List<String>): Color {
    val firstType = types.firstOrNull() ?: return Color.LightGray
    return getColorForType(firstType)
}



