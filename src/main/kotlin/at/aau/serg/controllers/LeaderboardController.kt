package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {
    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int? = null): List<GameResult> {
        val sortedResults = gameResultService.getGameResults().sortedWith(
            compareByDescending<GameResult> { it.score } // Score wird vergliechen
                .thenBy { it.timeInSeconds } // Zeit wrid vergliechen
        )
        if (rank == null) {
            return sortedResults
        }

        if (rank !in 1..sortedResults.size) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Rank must be between 1 and ${sortedResults.size}"
            )
        }

        val targetIndex = rank - 1
        val fromIndex = maxOf(0, targetIndex - 3)
        val toIndex = minOf(sortedResults.size, targetIndex + 4)

        return sortedResults.subList(fromIndex, toIndex)
    }
}