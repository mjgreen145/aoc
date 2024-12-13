package `2024`

import LineFormula
import println
import readInput
import kotlin.math.roundToLong
import kotlin.time.measureTime

typealias ClawGame = Pair<LineFormula, LineFormula>

fun main() {
    val exampleInput = readInput("2024", "day13-example")
    val input = readInput("2024", "day13")

    fun Double.isInt(): Boolean {
        return this % 1.0 == 0.0
    }

    fun parseGames(input: String, targetAdjustment: Long): List<ClawGame> {
        return input.split("\n\n").map { lines ->
            val (a, b, t) = lines.split("\n")
                .map { text -> Regex("(\\d+)\\D+(\\d+)").find(text)!!.groupValues.drop(1).map { it.toDouble() } }
            Pair(LineFormula(a[0], b[0], t[0] + targetAdjustment), LineFormula(a[1], b[1], t[1] + targetAdjustment))
        }
    }

    fun solveEquations(eq1: LineFormula, eq2: LineFormula): Pair<Double, Double>? {
        val (a1, b1, c1) = eq1;
        val (a2, b2, c2) = eq2;

        val numerator = (b1 * c2) - (b2 * c1)
        val denominator = (a2 * b1) - (b2 * a1)
        if (denominator == 0.0) return null

        val x = numerator / denominator
        val y = (c1 - a1 * x) / b1
        return Pair(x, y)
    }

    fun solveGame(game: ClawGame): Pair<Long, Long>? {
        val (eq1, eq2) = game
        val solution = solveEquations(eq1, eq2) ?: return null
        val times = Pair(solution.first.roundToLong(), solution.second.roundToLong())
        return if (solution.first.isInt() && solution.second.isInt()) times else null
    }

    fun solveAll(input: String, targetAdjustment: Long): Long {
        return parseGames(input, targetAdjustment).sumOf { game ->
            val solution = solveGame(game)
            solution?.let { (aTimes, bTimes) -> (3 * aTimes + bTimes) } ?: 0
        }
    }

    fun part1(input: String): Long = solveAll(input, 0L)
    fun part2(input: String): Long = solveAll(input, 10000000000000L)

    val part1Example = part1(exampleInput)

    check(part1Example == 480L) { -> "Part 1 example failed: Expected 480, received $part1Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}