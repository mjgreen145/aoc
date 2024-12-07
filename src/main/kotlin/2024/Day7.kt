package `2024`

import println
import readLines
import kotlin.time.measureTime

fun main() {
    val exampleLines = readLines("2024", "day7-example")
    val lines = readLines("2024", "day7")

    fun solvable(target: Long, current: Long, remainingNums: List<Long>): Boolean {
        if (current > target) return false
        if (remainingNums.isEmpty()) return target == current

        return solvable(target, current + remainingNums.first(), remainingNums.drop(1)) ||
                solvable(target, current * remainingNums.first(), remainingNums.drop(1))
    }

    fun solvable2(target: Long, current: Long, remainingNums: List<Long>): Boolean {
        if (current > target) return false
        if (remainingNums.isEmpty()) return target == current

        return solvable2(target, current + remainingNums.first(), remainingNums.drop(1)) ||
                solvable2(target, current * remainingNums.first(), remainingNums.drop(1)) ||
                solvable2(target, (current.toString() + remainingNums.first().toString()).toLong(), remainingNums.drop(1))
    }

    fun parseInput(lines: List<String>): List<Pair<Long, List<Long>>> {
        return lines.map { line ->
            val (target, nums) = line.split(": ")
            Pair(target.toLong(), nums.split(" ").map { it.toLong() })
        }
    }

    fun part1(lines: List<String>): Long {
        return parseInput(lines).filter { (target, nums) -> solvable(target, nums.first(), nums.drop(1)) }
            .sumOf { it.first }
    }

    fun part2(lines: List<String>): Long {
        return parseInput(lines).filter { (target, nums) -> solvable2(target, nums.first(), nums.drop(1)) }
            .sumOf { it.first }
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 3749L) { -> "Part 1 example failed: Expected 3749, received $part1Example" };
    check(part2Example == 11387L) { -> "Part 2 example failed: Expected 11387, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}