package `2024`

import println
import readLines
import kotlin.math.abs
import kotlin.time.measureTime

enum class Dir {
    Asc, Desc
}

fun isSafeInDir(xs: List<Int>, dir: Dir): Boolean {
    return xs.windowed(2)
        .all { (a, b) -> a != b && abs(a - b) <= 3 && ((dir == Dir.Asc && a < b) || (dir == Dir.Desc && b < a)) }
}

fun isSafe(xs: List<Int>): Boolean {
    if (xs.size < 2) return true
    if (xs[0] == xs[1]) return false

    val dir = if (xs[0] < xs[1]) Dir.Asc else Dir.Desc
    return isSafeInDir(xs, dir);
}

fun allListsOneRemoved(xs: List<Int>): List<List<Int>> {
    return xs.indices.map { idx ->
        val newList = xs.toMutableList()
        newList.removeAt(idx)
        newList
    }
}

fun main() {
    val exampleLines = readLines("2024", "day2-example")
    val lines = readLines("2024", "day2")

    fun part1(lines: List<String>): Int {
        return lines.count { line -> isSafe(line.split(" ").map { c -> c.toInt() }) }
    }

    fun part2(lines: List<String>): Int {
        return lines.count { line ->
            val ints = line.split(" ").map { c -> c.toInt() }
            val allPossible = allListsOneRemoved(ints)
            allPossible.any(::isSafe)
        }
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 2) { -> "Part 1 example failed: Expected 2, received $part1Example" };
    check(part2Example == 4) { -> "Part 2 example failed: Expected 4, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}