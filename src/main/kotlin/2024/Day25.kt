package `2024`

import println
import readInput
import kotlin.time.measureTime

fun main() {
    val exampleInput = readInput("2024", "day25-example")
    val input = readInput("2024", "day25")

    fun toHeights(input: List<String>): List<Int> {
        return input.first().indices.map { i -> input.map { it[i] }.count { it == '#' } - 1 }
    }

    fun part1(text: String): Int {
        val (lockGrids, keyGrids) = text.split("\n\n")
            .map { it.split("\n") }
            .partition { str -> str.first().all { it == '#' } }
        val locks = lockGrids.map(::toHeights)
        val keys = keyGrids.map(::toHeights)

        return locks.sumOf { lock ->
            keys.count { key ->
                lock.zip(key) { a, b -> a + b }.all { it < 6 }
            }
        }
    }

    val part1Example = part1(exampleInput)
    check(part1Example == 3) { -> "Part 1 example failed: Expected 3, received $part1Example" };
    val timePart1 = measureTime { part1(input).println() }
    println("Part 1 took $timePart1")
}