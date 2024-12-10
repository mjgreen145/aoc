package `2024`

import Coord
import Grid
import adjacentCoords
import allCoords
import get
import println
import readLines
import kotlin.time.measureTime

fun main() {
    val exampleLines = readLines("2024", "day10-example")
    val lines = readLines("2024", "day10")

    fun findApexes(current: Coord, grid: Grid): Set<Coord> {
        val height = grid.get(current).digitToInt()
        if (height == 9) return setOf(current)
        val nextSteps = grid.adjacentCoords(current).filter { grid.get(it).digitToInt() == height + 1 }
        return nextSteps.flatMap { findApexes(it, grid) }.toSet()
    }

    fun numPaths(current: Coord, grid: Grid): Int {
        val height = grid.get(current).digitToInt()
        if (height == 9) return 1
        val nextSteps = grid.adjacentCoords(current).filter { grid.get(it).digitToInt() == height + 1 }
        return nextSteps.sumOf { numPaths(it, grid) }
    }

    fun part1(grid: Grid): Int {
        val starts = grid.allCoords().filter { grid.get(it) == '0' }
        return starts.sumOf { findApexes(it, grid).size }
    }

    fun part2(grid: Grid): Int {
        val starts = grid.allCoords().filter { grid.get(it) == '0' }
        return starts.sumOf { numPaths(it, grid) }
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 36) { -> "Part 1 example failed: Expected 36, received $part1Example" };
    check(part2Example == 81) { -> "Part 2 example failed: Expected 81, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}