package `2024`

import println
import readInput
import kotlin.math.roundToInt
import kotlin.time.measureTime

typealias Matrix = Array<Array<Double>>

fun main() {
    val exampleInput = readInput("2024", "day13-example")
    val input = readInput("2024", "day13")

    fun parseGames(input: String): List<Triple<List<Double>, List<Double>, List<Double>>> {
        return input.split("\n\n").map { lines ->
            val (aTxt, bTxt, prizeTxt) = lines.split("\n")
            val a = Regex("\\+(\\d+).*\\+(\\d+)").find(aTxt)!!.groupValues.drop(1).map { it.toDouble() }
            val b = Regex("\\+(\\d+).*\\+(\\d+)").find(bTxt)!!.groupValues.drop(1).map { it.toDouble() }
            val p = Regex("=(\\d+).*=(\\d+)").find(prizeTxt)!!.groupValues.drop(1).map { it.toDouble() }
            Triple(a, b, p)
        }
    }

    fun matrixInverse(matrix: Matrix): Matrix? {
        check(matrix.size == 2 && matrix[0].size == 2 && matrix[1].size == 2)
        val (a, b) = matrix[0]
        val (c, d) = matrix[1]

        val determinant = a * d - b * c
        if (determinant == 0.0) {
            return null
        }
        val cooeficient = 1 / determinant

        return arrayOf(
            arrayOf(cooeficient * d, cooeficient * -b),
            arrayOf(cooeficient * -c, cooeficient * a),
        )
    }

    fun vectorMultiply(u: Array<Double>, v: Array<Double>): Double {
        check(u.size == v.size)
        return (v zip u).sumOf { (x, y) -> x * y }
    }

    fun matrixMultiply(A: Matrix, B: Matrix): Matrix {
        return A.map { aRow ->
            B[0].mapIndexed { i, _ ->
                vectorMultiply(aRow, B.map { it[i] }.toTypedArray())
            }.toTypedArray()
        }.toTypedArray()
    }

    fun part1(input: String): Int {
        val games = parseGames(input)
        return games.sumOf { (a, b, p) ->
            val prizeVector = arrayOf(arrayOf(p[0]), arrayOf(p[1]))
            val matrix = arrayOf(arrayOf(a[0], b[0]), arrayOf(a[1], b[1]))
            val inverse = matrixInverse(matrix)
            if (inverse != null) {
                val (aTimes, bTimes) = matrixMultiply(inverse, prizeVector).map { it[0].roundToInt() }
                Pair(aTimes, bTimes).println()
                if (aTimes in 0..100 && bTimes in 0..100) (3 * aTimes + bTimes) else 0
            } else {
                0
            }
        }
    }

    fun part2(input: String): Int {
        return 0
    }

    val part1Example = part1(exampleInput)
    val part2Example = part2(exampleInput)

    check(part1Example == 480) { -> "Part 1 example failed: Expected 480, received $part1Example" };
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}