package `2023`

import pivotToCols
import println
import readInput
import java.lang.Exception
import kotlin.time.measureTime

fun editDistance(str1: String, str2: String): Int {
    return str1.mapIndexed { index, c -> c != str2[index] }.count { it }
}

fun findMirror(block: String, smudgesAllowed: Int = 0): Int {
    val lines = block.split("\n");
    val horizontalMatch = findReflectionIndex(lines, smudgesAllowed);
    if (horizontalMatch != null) {
        return horizontalMatch * 100
    }

    val verticalMatch = findReflectionIndex(pivotToCols(lines), smudgesAllowed)
    if (verticalMatch != null) {
        return verticalMatch
    }

    throw Exception("Failed to find any match")
}

fun findReflectionIndex(lines: List<String>, smudgesAllowed: Int): Int? {
    val reflectedLineIndexes = List(lines.size) { i -> i }
        .filter { i ->
            i + 1 < lines.size && editDistance(lines[i], lines[i + 1]) <= smudgesAllowed
        }

    reflectedLineIndexes.forEach { index ->
        var smudgesLeft = smudgesAllowed;
        for (i in 0..index) {
            val editDistance = editDistance(lines[index - i], lines[index + i + 1])
            if (editDistance > smudgesLeft) {
                break
            }
            smudgesLeft -= editDistance
            if (index - i == 0 || index + i + 1 == lines.size - 1) {
                if (smudgesLeft == 0) {
                    return index + 1
                } else {
                    break;
                };
            }
        }
    }
    return null
}

fun main() {
    val exampleBlocks = readInput("2023", "day13-example").split("\n\n")
    val blocks = readInput("2023", "day13").split("\n\n")

    fun part1(blocks: List<String>): Int = blocks.sumOf(::findMirror)

    fun part2(blocks: List<String>): Int {
        return blocks.sumOf { findMirror(it, 1) }
    }

    val part1Example = part1(exampleBlocks)
    check(part1Example == 405) { -> "Part 1 example failed: Expected 405, received $part1Example" };

    val timePart1 = measureTime { part1(blocks).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleBlocks)
    check(part2Example == 400) { -> "Part 2 example failed: Expected 400, received $part2Example" };

    val timePart2 = measureTime { part2(blocks).println() }
    println("Part 2 took $timePart2")
}