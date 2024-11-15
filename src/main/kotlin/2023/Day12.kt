package `2023`

import println
import readLines
import kotlin.time.measureTime

val knownCombinations = mutableMapOf<Pair<String, List<Int>>, Long>()

fun numCombinations(springs: String, groups: List<Int>): Long {
    if (springs == "") {
        return if (groups.isEmpty()) 1 else 0
    }
    if (groups.isEmpty() && springs.contains('#')) return 0

    if (springs.first() == '.') return numCombinationsCached(springs.drop(1), groups)

    if (springs.first() == '#') {
        val nextGroup = groups.first()
        val nextChunk = springs.take(nextGroup)
        val rest = springs.drop(nextGroup)
        if (nextChunk.contains('.') || (rest.isNotEmpty() && rest.first() == '#') || nextChunk.length < nextGroup) return 0

        return numCombinationsCached(springs.drop(nextGroup + 1), groups.drop(1))
    }

    val replaceWithDot = numCombinationsCached(".${springs.drop(1)}", groups)
    val replaceWithHash = numCombinationsCached("#${springs.drop(1)}", groups)

    return replaceWithDot + replaceWithHash
}

fun numCombinationsCached(springs: String, groups: List<Int>): Long {
    val key = Pair(springs, groups)
    val value = knownCombinations[key]
    if (value != null) {
        return value
    }

    val answer = numCombinations(springs, groups)
    knownCombinations[key] = answer
    return answer
}

fun main() {
    val exampleLines = readLines("2023", "day12-example")
    val lines = readLines("2023", "day12")

    fun part1(lines: List<String>): Long {
        return lines.sumOf { line ->
            val (springs, groupsStr) = line.split(" ")
            val groups = groupsStr.split(",").map { it.toInt() }
            numCombinationsCached(springs, groups)
        }
    }

    fun part2(lines: List<String>): Long {
        return lines.mapIndexed { i, line ->
            val (springs, groupsStr) = line.split(" ")
            val groups = groupsStr.split(",").map { it.toInt() }

            numCombinationsCached(Array(5) { springs }.joinToString("?"), Array(5) { groups }.flatMap { it })
        }.sum()
    }

    val part1Example = part1(exampleLines)
    check(part1Example == 21L) { -> "Part 1 example failed: Expected 21, received $part1Example" };

    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleLines)
    check(part2Example == 525152L) { -> "Part 2 example failed: Expected 525152, received $part2Example" };

    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")
}