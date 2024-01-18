import com.example.pokedex.data.NetworkPokedexRepository
import com.example.pokedex.model.Evolution
import com.example.pokedex.model.Pokemon
import com.example.pokedex.services.PokedexApiService
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class NetworkPokedexRepositoryTest {

    private lateinit var repository: NetworkPokedexRepository
    private lateinit var mockApiService: PokedexApiService

    @Before
    fun setUp() {
        mockApiService = Mockito.mock(PokedexApiService::class.java)
        repository = NetworkPokedexRepository(mockApiService)
    }

    @Test
    fun getPokemons_returnsListOfPokemons() = runBlocking {
        // Arrange
        val expectedPokemons = listOf(
            Pokemon(1, "Bulbasaur", listOf("Grass", "Poison"), "description", "image_url", Evolution(emptyList(), listOf(2)))
        )
        `when`(mockApiService.getPokemons()).thenReturn(expectedPokemons)

        // Act
        val pokemons = repository.getPokemons()

        // Assert
        assertEquals(expectedPokemons, pokemons)
    }

    @Test
    fun getPokemonDetailsById_returnsPokemon() = runBlocking {
        // Arrange
        val pokemonId = 1
        val expectedPokemon = Pokemon(pokemonId, "Bulbasaur", listOf("Grass", "Poison"), "description", "image_url", Evolution(emptyList(), listOf(2)))
        `when`(mockApiService.getPokemonById(pokemonId)).thenReturn(expectedPokemon)

        // Act
        val pokemon = repository.getPokemonDetailsById(pokemonId)

        // Assert
        assertEquals(expectedPokemon, pokemon)
    }

    @Test
    fun getPokemons_onHttpException_catchesException() = runBlocking {
        // Arrange
        val httpException = HttpException(Response.error<Any>(500, ResponseBody.create(null, "")))
        `when`(mockApiService.getPokemons()).thenThrow(httpException)

        // Act
        val result = runCatching { repository.getPokemons() }

        // Assert
        assertNotNull(result.exceptionOrNull())
        assertTrue(result.exceptionOrNull() is HttpException)
    }
    @Test
    fun getPokemonDetailsById_onHttpException_throwsException() = runBlocking {
        // Arrange
        val pokemonId = 1
        `when`(mockApiService.getPokemonById(pokemonId)).thenThrow(HttpException::class.java)

        // Act
        val result = runCatching { repository.getPokemonDetailsById(pokemonId) }

        // Assert
        assertNotNull(result.exceptionOrNull())
        assert(result.exceptionOrNull() is HttpException)
    }
}
