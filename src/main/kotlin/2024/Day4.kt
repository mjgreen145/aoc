package `2024`

import Dir8
import Grid
import allCoords
import getOrEmpty
import move
import println
import readLines
import kotlin.time.measureTime

fun main() {
    val exampleLines = readLines("2024", "day4-example")
    val lines = readLines("2024", "day4")

    fun part1(grid: Grid): Int {
        val range = 0..3
        return grid.allCoords().sumOf { c ->
            Dir8.entries.map { dir -> range.joinToString("") { dist -> grid.getOrEmpty(c.move(dir, dist)) } }
                .count { it == "XMAS" }
        }
    }

    fun part2(grid: Grid): Int {
        return grid.allCoords().count { c ->
            val range = -1..1
            listOf(Dir8.NE, Dir8.NW)
                .map { dir -> range.joinToString("") { dist -> grid.getOrEmpty(c.move(dir, dist)) } }
                .all { w -> w == "MAS" || w == "SAM" }
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