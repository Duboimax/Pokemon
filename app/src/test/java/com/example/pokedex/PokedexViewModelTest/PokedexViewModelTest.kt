package com.example.pokedex.PokedexViewModelTest

import com.example.pokedex.data.PokedexRepository
import com.example.pokedex.model.Evolution
import com.example.pokedex.model.Pokemon
import com.example.pokedex.ui.screens.PokedexUiState
import com.example.pokedex.ui.screens.PokedexViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class PokedexViewModelTest {

    private lateinit var viewModel: PokedexViewModel
    private val pokedexRepository = mock(PokedexRepository::class.java)
    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val mockPokemons = listOf(
        Pokemon(
            id = 1,
            name = "Bulbasaur",
            type = listOf("Grass", "Poison"),
            description = "A strange seed was planted on its back at birth. The plant sprouts and grows with this Pokémon.",
            image_url = "https://example.com/bulbasaur.png",
            evolutions = Evolution(
                before = emptyList(),
                after = listOf(2)
            )
        ),
        Pokemon(
            id = 2,
            name = "Ivysaur",
            type = listOf("Grass", "Poison"),
            description = "When the bulb on its back grows large, it appears to lose the ability to stand on its hind legs.",
            image_url = "https://example.com/ivysaur.png",
            evolutions = Evolution(
                before = listOf(1),
                after = listOf(3)
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testCoroutineDispatcher)
        viewModel = PokedexViewModel(pokedexRepository)
        assertEquals(PokedexUiState.Loading, viewModel.pokedexUiState) // État initial
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getPokemon_initialLoad_updatesPokemonList() = testCoroutineDispatcher.runBlockingTest {
        `when`(pokedexRepository.getPokemons()).thenReturn(mockPokemons)

        viewModel.getPokemon()

        assertEquals(PokedexUiState.Succes(mockPokemons), viewModel.pokedexUiState)
        assertEquals(mockPokemons, viewModel.pokemonList)
    }

    @Test
    fun getPokemon_withIOException_setsErrorState() = testCoroutineDispatcher.runBlockingTest {
        // Arrange
        doAnswer { throw IOException() }.`when`(pokedexRepository).getPokemons()

        // Act
        viewModel.getPokemon()

        // Assert
        assertEquals(PokedexUiState.Error, viewModel.pokedexUiState)
    }

    @Test(expected = NoSuchElementException::class)
    fun getPokemonById_withInvalidId_throwsException() = testCoroutineDispatcher.runBlockingTest {
        // Arrange
        viewModel.setTestPokemonList(mockPokemons)

        // Act
        viewModel.getPokemonById(999) // ID non existant
    }

    @Test
    fun getPokemonById_returnsCorrectPokemon() = testCoroutineDispatcher.runBlockingTest {
        // Arrange
        val expectedPokemon = mockPokemons[0]
        viewModel.setTestPokemonList(mockPokemons)

        // Act
        val pokemon = viewModel.getPokemonById(1)

        // Assert
        assertEquals(expectedPokemon, pokemon)
    }

    @Test
    fun getEvolutionsById_returnsCorrectEvolutions() = testCoroutineDispatcher.runBlockingTest {
        // Arrange
        val expectedEvolutions = listOf(mockPokemons[1])
        viewModel.setTestPokemonList(mockPokemons)

        // Act
        val evolutions = viewModel.getEvolutionsById(listOf(2))

        // Assert
        assertEquals(expectedEvolutions, evolutions)

    }

    @Test
    fun refreshPokemons_updatesStateCorrectly() = testCoroutineDispatcher.runBlockingTest {
        // Arrange
        `when`(pokedexRepository.getPokemons()).thenReturn(mockPokemons)

        // Act
        viewModel.refreshPokemons()

        // Assert
        assertEquals(PokedexUiState.Succes(mockPokemons), viewModel.pokedexUiState)
        assertEquals(mockPokemons, viewModel.pokemonList)
        assertFalse(viewModel.isRefreshing)
    }

    @Test
    fun refreshPokemons_withHttpException_setsErrorState() = testCoroutineDispatcher.runBlockingTest {
        // Arrange
        `when`(pokedexRepository.getPokemons()).thenThrow(HttpException(Response.error<Any>(404, ResponseBody.create(null, ""))))

        // Act
        viewModel.refreshPokemons()

        // Assert
        assertEquals(PokedexUiState.Error, viewModel.pokedexUiState)
    }
}