package `2024`

import println
import readInput
import kotlin.time.measureTime

fun blink(stone: Long): List<Long> {
    if (stone == 0L) return listOf(1L)
    val str = stone.toString()
    if (str.length % 2 == 0) {
        return listOf(
            str.substring(0, str.length / 2).toLong(),
            str.substring(str.length / 2).toLong()
        )
    }
    return listOf((stone * 2024L))
}

val cache = mutableMapOf<Pair<Long, Int>, Long>()

fun numStonesGenerated(stone: Long, blinksLeft: Int): Long {
    if (blinksLeft == 0) return 1
    return blink(stone).sumOf { numStonesGeneratedCached(it, blinksLeft - 1) }
}

fun numStonesGeneratedCached(stone: Long, blinksLeft: Int): Long {
    val cached = cache[Pair(stone, blinksLeft)]
    if (cached != null) return cached

    val ans = numStonesGenerated(stone, blinksLeft)
    cache[Pair(stone, blinksLeft)] = ans
    return ans
}

fun main() {
    val exampleLines = readInput("2024", "day11-example")
    val input = readInput("2024", "day11")

    fun getStonesGenerated(input: String, blinks: Int): Long {
        val startStones = input.split(" ").map { it.toLong() }
        return startStones.sumOf { numStonesGeneratedCached(it, blinks) }
    }

    fun part1(input: String): Long {
        return getStonesGenerated(input, 25)
    }

    fun part2(input: String): Long {
        return getStonesGenerated(input, 75)
    }

    val part1Example = part1(exampleLines)

    check(part1Example == 55312L) { -> "Part 1 example failed: Expected 55312, received $part1Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}