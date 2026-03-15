package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when` as whenever
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        mockedService = mock(GameResultService::class.java)
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getGameResult_existingId_returnsObject() {
        val gameResult = GameResult(1, "player1", 100, 12.5)
        whenever(mockedService.getGameResult(1)).thenReturn(gameResult)

        val result = controller.getGameResult(1)

        verify(mockedService).getGameResult(1)
        assertEquals(gameResult, result)
    }

    @Test
    fun test_getGameResult_nonExistingId_returnsNull() {
        whenever(mockedService.getGameResult(99)).thenReturn(null)

        val result = controller.getGameResult(99)

        verify(mockedService).getGameResult(99)
        assertNull(result)
    }

    @Test
    fun test_getAllGameResults_returnsAllResults() {
        val result1 = GameResult(1, "Anna", 100, 10.0)
        val result2 = GameResult(2, "Ben", 90, 11.0)
        whenever(mockedService.getGameResults()).thenReturn(listOf(result1, result2))

        val result = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(2, result.size)
        assertEquals(result1, result[0])
        assertEquals(result2, result[1])
    }

    @Test
    fun test_addGameResult_delegatesToService() {
        val gameResult = GameResult(0, "Clara", 80, 13.2)

        controller.addGameResult(gameResult)

        verify(mockedService).addGameResult(gameResult)
    }

    @Test
    fun test_deleteGameResult_delegatesToService() {
        controller.deleteGameResult(5)

        verify(mockedService).deleteGameResult(5)
    }
}