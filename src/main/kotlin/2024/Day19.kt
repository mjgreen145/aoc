package `2024`

import println
import readInput
import kotlin.time.measureTime

fun main() {
    val exampleInput = readInput("2024", "day19-example")
    val input = readInput("2024", "day19")

    fun numSolutions(design: String, towels: List<String>, knownSolutions: MutableMap<String, Long>): Long {
        return knownSolutions.getOrPut(design) {
            if (design == "") return@getOrPut 1
            towels.filter { design.startsWith(it) }
                .sumOf { towel -> numSolutions(design.drop(towel.length), towels, knownSolutions) }
        }
    }

    fun parseInput(text: String): Pair<List<String>, List<String>> {
        val (patternText, designText) = text.split("\n\n")
        val towels = patternText.split(", ")
        val designs = designText.split("\n")
        return Pair(towels, designs)
    }

    fun part1(text: String): Long {
        val (towels, designs) = parseInput(text)
        val knownSolutions = mutableMapOf<String, Long>()
        return designs.map { numSolutions(it, towels, knownSolutions) }.count { it != 0L }.toLong()
    }

    fun part2(text: String): Long {
        val (towels, designs) = parseInput(text)
        val knownSolutions = mutableMapOf<String, Long>()
        return designs.sumOf { numSolutions(it, towels, knownSolutions) }
    }

    val part1Example = part1(exampleInput)
    val part2Example = part2(exampleInput)

    check(part1Example == 6L) { -> "Part 1 example failed: Expected 6, received $part1Example" };
    check(part2Example == 16L) { -> "Part 2 example failed: Expected 16, received $part2Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}