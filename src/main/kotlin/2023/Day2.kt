package `2023`

import println
import java.lang.Exception

data class GameRound(val red: Int, val blue: Int, val green: Int)

data class Game(val gameNumber: Int, val rounds: List<GameRound>)

fun getDiceNumber(roundLine: String, colour: String): Int {
    val match = Regex("(\\d+) ${colour}").find(roundLine)
    return if (match != null) match.groupValues[1].toInt() else 0
}

fun parseRoundLine(roundLine: String): GameRound {
    return GameRound(
        red = getDiceNumber(roundLine, "red"),
        blue = getDiceNumber(roundLine, "blue"),
        green = getDiceNumber(roundLine, "green"),
    )
}

fun parseGameLine(gameLine: String): Game {
    val regex = Regex("^Game (\\d+)")
    val match = regex.find(gameLine)
    if (match != null) {
        val gameNumber = match.groupValues[1].toInt()
        val roundsText = gameLine.split(":").last

        return Game(
            gameNumber = gameNumber,
            rounds = roundsText.split(";").map { round -> parseRoundLine(round) }
        )
    }

    throw Exception("Failed to parse game line")
}

fun main() {
    val text = object {}.javaClass.getResourceAsStream("day2.txt")?.bufferedReader()?.readText() ?: return
    val lines = text.split("\n");

    fun part1(lines: List<String>): Int {
        val games = lines.map { line -> parseGameLine(line) }

        val validGames = games.filter { game ->
            game.rounds.all { round -> round.red <= 12 && round.green <= 13 && round.blue <= 14 }
        }
        return validGames.sumOf { game -> game.gameNumber }
    }

    fun part2(lines: List<String>): Int {
        val games = lines.map { line -> parseGameLine(line) }

        return games.sumOf { game ->
            val maxRed = game.rounds.map { round -> round.red }.max()
            val maxBlue = game.rounds.map { round -> round.blue }.max()
            val maxGreen = game.rounds.map { round -> round.green }.max()

            maxRed * maxBlue * maxGreen
        }
    }

    part1(lines).println();
    part2(lines).println();
}