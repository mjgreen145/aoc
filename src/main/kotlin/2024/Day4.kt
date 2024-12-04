package `2024`

import Grid
import getOrEmpty
import println
import readLines
import kotlin.time.measureTime

fun xmasesStartingAt(x: Int, y: Int, g: Grid): Int {
    val w1 = g.getOrEmpty(x, y) + g.getOrEmpty(x + 1, y) + g.getOrEmpty(x + 2, y) + g.getOrEmpty(x + 3, y)
    val w2 = g.getOrEmpty(x, y) + g.getOrEmpty(x - 1, y) + g.getOrEmpty(x - 2, y) + g.getOrEmpty(x - 3, y)
    val w3 = g.getOrEmpty(x, y) + g.getOrEmpty(x, y + 1) + g.getOrEmpty(x, y + 2) + g.getOrEmpty(x, y + 3)
    val w4 = g.getOrEmpty(x, y) + g.getOrEmpty(x, y - 1) + g.getOrEmpty(x, y - 2) + g.getOrEmpty(x, y - 3)
    val w5 = g.getOrEmpty(x, y) + g.getOrEmpty(x + 1, y + 1) + g.getOrEmpty(x + 2, y + 2) + g.getOrEmpty(x + 3, y + 3)
    val w6 = g.getOrEmpty(x, y) + g.getOrEmpty(x + 1, y - 1) + g.getOrEmpty(x + 2, y - 2) + g.getOrEmpty(x + 3, y - 3)
    val w7 = g.getOrEmpty(x, y) + g.getOrEmpty(x - 1, y + 1) + g.getOrEmpty(x - 2, y + 2) + g.getOrEmpty(x - 3, y + 3)
    val w8 = g.getOrEmpty(x, y) + g.getOrEmpty(x - 1, y - 1) + g.getOrEmpty(x - 2, y - 2) + g.getOrEmpty(x - 3, y - 3)

    return listOf(w1, w2, w3, w4, w5, w6, w7, w8).count { it == "XMAS" }
}

fun masInAnX(x: Int, y: Int, g: Grid): Boolean {
    val w1 = g.getOrEmpty(x - 1, y - 1) + g.getOrEmpty(x, y) + g.getOrEmpty(x + 1, y + 1)
    val w2 = g.getOrEmpty(x + 1, y - 1) + g.getOrEmpty(x, y) + g.getOrEmpty(x - 1, y + 1)

    return (w1 == "MAS" || w1 == "SAM") && (w2 == "MAS" || w2 == "SAM")
}

fun main() {
    val exampleLines = readLines("2024", "day4-example")
    val lines = readLines("2024", "day4")

    fun part1(lines: List<String>): Int {
        return lines.indices.sumOf { y ->
            lines[y].indices.sumOf { x -> xmasesStartingAt(x, y, lines) }
        }
    }

    fun part2(lines: List<String>): Int {
        return lines.indices.sumOf { y ->
            lines[y].indices.count { x -> masInAnX(x, y, lines) }
        }
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 18) { -> "Part 1 example failed: Expected 18, received $part1Example" };
    check(part2Example == 9) { -> "Part 2 example failed: Expected 9, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}