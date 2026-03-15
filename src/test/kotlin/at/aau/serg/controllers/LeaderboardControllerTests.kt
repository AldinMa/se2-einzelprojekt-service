package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever// when is a reserved keyword in Kotlin
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard()

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectTimeInSecondsSorting() { // timeInSeconds im test angepasst(vom kleinsten zum größten)
        val first = GameResult(1, "first", 20, 10.0)
        val second = GameResult(2, "second", 20, 15.0)
        val third = GameResult(3, "third", 20, 20.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard()

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_withRank_returnsPlayerAndNeighbors() {
        val p1 = GameResult(1, "p1", 100, 10.0)
        val p2 = GameResult(2, "p2", 90, 11.0)
        val p3 = GameResult(3, "p3", 80, 12.0)
        val p4 = GameResult(4, "p4", 70, 13.0)
        val p5 = GameResult(5, "p5", 60, 14.0)
        val p6 = GameResult(6, "p6", 50, 15.0)
        val p7 = GameResult(7, "p7", 40, 16.0)
        val p8 = GameResult(8, "p8", 30, 17.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(p8, p6, p4, p2, p7, p1, p5, p3))

        val res = controller.getLeaderboard(4)

        verify(mockedService).getGameResults()
        assertEquals(listOf(p1, p2, p3, p4, p5, p6, p7), res)
    }

    @Test
    fun test_getLeaderboard_withRankAtStart_returnsOnlyAvailableUpperWindow() {
        val p1 = GameResult(1, "p1", 100, 10.0)
        val p2 = GameResult(2, "p2", 90, 11.0)
        val p3 = GameResult(3, "p3", 80, 12.0)
        val p4 = GameResult(4, "p4", 70, 13.0)
        val p5 = GameResult(5, "p5", 60, 14.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(p5, p3, p1, p4, p2))

        val res = controller.getLeaderboard(1)

        verify(mockedService).getGameResults()
        assertEquals(listOf(p1, p2, p3, p4), res)
    }

    @Test
    fun test_getLeaderboard_withRankTooLarge_throwsBadRequest() {
        val p1 = GameResult(1, "p1", 100, 10.0)
        val p2 = GameResult(2, "p2", 90, 11.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(p2, p1))

        val ex = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(3)
        }

        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
    }

    @Test
    fun test_getLeaderboard_withNegativeRank_throwsBadRequest() {
        val p1 = GameResult(1, "p1", 100, 10.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(p1))

        val ex = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(-1)
        }

        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
    }

}