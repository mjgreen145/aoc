package `2024`

import println
import readInput
import kotlin.time.measureTime

fun main() {
    val example = readInput("2024", "day3-example")
    val examplePart2 = readInput("2024", "day3-example2")
    val text = readInput("2024", "day3")

    fun sumMults(text: String): Int {
        return Regex("mul\\((\\d+),(\\d+)\\)").findAll(text).sumOf { match ->
            match.groupValues[1].toInt() * match.groupValues[2].toInt()
        }
    }

    fun part1(text: String): Int {
        return sumMults(text)
    }

    fun part2(text: String): Int {
        return text.split("do()")
            .map { it.split("don't()").first() }
            .sumOf(::sumMults)
    }

    val part1Example = part1(example)
    val part2Example = part2(examplePart2)

    check(part1Example == 161) { -> "Part 1 example failed: Expected 161, received $part1Example" };
    check(part2Example == 48) { -> "Part 2 example failed: Expected 48, received $part2Example" };

    val timePart1 = measureTime { part1(text).println() }
    val timePart2 = measureTime { part2(text).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}