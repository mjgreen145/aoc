package `2024`

import Dir
import Grid
import findChar
import get
import move
import println
import readLines
import x
import y
import kotlin.time.measureTime

fun main() {
    val exampleInput = readLines("2024", "day21-example")
    val input = readLines("2024", "day21")

    fun paths(keypad: Grid, startChar: Char, endChar: Char): List<String> {
        val start = keypad.findChar(startChar)
        val end = keypad.findChar(endChar)
        val toCheck = mutableListOf(Pair(start, ""))
        val paths = mutableListOf<String>()

        while (toCheck.isNotEmpty()) {
            val (curr, path) = toCheck.removeFirst()
            if (curr == end) {
                paths.add(path)
                continue
            }
            val diffX = end.x() - curr.x()
            val diffY = end.y() - curr.y()
            if (diffX != 0) {
                val nextX = curr.move(Dir.East, if (diffX > 0) 1 else -1)
                if (keypad.get(nextX) != ' ') toCheck.add(Pair(nextX, path + if (diffX > 0) '>' else '<'))
            }
            if (diffY != 0) {
                val nextY = curr.move(Dir.South, if (diffY > 0) 1 else -1)
                if (keypad.get(nextY) != ' ') toCheck.add(Pair(nextY, path + if (diffY > 0) 'v' else '^'))
            }
        }

        return paths.map { it + 'A' }
    }

    fun splitAfter(string: String, char: Char): List<String> {
        return Regex("[^$char]*$char").findAll(string).map { it.value }.toList()
    }

    fun keypadCombinations(code: String, keypad: Grid): List<String> {
        val combinations = "A$code".windowed(2).map { paths(keypad, it[0], it[1]) }
        return combinations.reduce { currentStrs, nextStrs ->
            currentStrs.flatMap { c -> nextStrs.map { n -> c + n } }
        }
    }

    val numPad: Grid = listOf("789", "456", "123", " 0A")
    val dirPad = listOf(" ^A", "<v>")
    var cache = mutableMapOf<Pair<String, Int>, Long>();
    fun shortestDirSeq(input: String, depth: Int): Long {
        return splitAfter(input, 'A').sumOf { subInput ->
            cache.getOrPut(Pair(subInput, depth)) {
                val combinations = keypadCombinations(subInput, dirPad)
                val min = combinations.minOf { it.length.toLong() }
                if (depth == 1) min else combinations.minOf { shortestDirSeq(it, depth - 1) }
            }
        }
    }

    fun shortestSeq(input: String, numDirKeypads: Int): Long {
        val a = keypadCombinations(input, numPad)
        return a.minOf { shortestDirSeq(it, numDirKeypads) }
    }

    fun part1(lines: List<String>): Long {
        return lines.sumOf { shortestSeq(it, 2) * it.dropLast(1).toLong() }
    }

    fun part2(lines: List<String>): Long {
        return lines.sumOf { shortestSeq(it, 25) * it.dropLast(1).toLong() }
    }

    val part1Example = part1(exampleInput)

    check(part1Example == 126384L) { -> "Part 1 example failed: Expected 126384, received $part1Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}