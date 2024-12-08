package `2024`

import Coord
import Grid
import add
import get
import allCoords
import containsCoord
import minus
import mul
import println
import readLines
import kotlin.time.measureTime

fun main() {
    val exampleLines = readLines("2024", "day8-example")
    val lines = readLines("2024", "day8")

    fun <T> pairs(xs: List<T>): List<Pair<T, T>> {
        return xs.flatMapIndexed { i: Int, x1: T -> xs.drop(i + 1).map { x2 -> Pair(x1, x2) } }
    }

    fun solveWith(grid: Grid, getAntinodes: (a: Coord, b: Coord) -> List<Coord>): Int {
        val freqs = grid.flatMap { it.toSet() }.toSet().filter { it != '.' }
        return freqs.flatMap { char ->
            val coords = grid.allCoords().filter { grid.get(it) == char }
            pairs(coords).flatMap { (a, b) -> getAntinodes(a, b) }
        }.toSet().count { grid.containsCoord(it) }
    }

    fun getAntinodes1(a: Coord, b: Coord): List<Coord> {
        val diff = b.minus(a)
        return listOf(b.add(diff), a.minus(diff))
    }

    fun getAntinodes2(a: Coord, b: Coord, gridSize: Int): List<Coord> {
        val diff = b.minus(a)
        return (-gridSize..gridSize).map { a.add(diff.mul(it)) }
    }

    fun part1(grid: Grid): Int {
        return solveWith(grid, ::getAntinodes1)
    }

    fun part2(grid: Grid): Int {
        return solveWith(grid) { a, b -> getAntinodes2(a, b, grid.size) }
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 14) { -> "Part 1 example failed: Expected 14, received $part1Example" };
    check(part2Example == 34) { -> "Part 2 example failed: Expected 34, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}