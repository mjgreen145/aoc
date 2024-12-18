package `2024`

import Coord
import Grid
import adjacentCoords
import gridOfSize
import readLines
import println
import x
import y
import kotlin.time.measureTime

fun main() {
    val exampleInput = readLines("2024", "day18-example")
    val input = readLines("2024", "day18")

    fun printGrid(bytes: Set<Coord>, grid: Grid) {
        grid.mapIndexed { y, _ ->
            grid[y].mapIndexed { x, _ ->
                if (bytes.contains(Pair(x, y))) '#'
                else '.'
            }.joinToString("")
        }.joinToString("\n").println()
    }

    fun minSteps(bytes: Set<Coord>, grid: Grid, start: Coord, end: Coord): Int? {
        val toCheck = mutableListOf(Pair(start, 0))
        val checked = mutableMapOf<Coord, Int>()

        while (toCheck.isNotEmpty()) {
            val (coord, steps) = toCheck.removeFirst()
            if (checked.getOrDefault(coord, Int.MAX_VALUE) <= steps) continue
            checked[coord] = steps
            if (coord == end) continue

            toCheck.addAll(grid.adjacentCoords(coord).filter { !bytes.contains(it) }.map { Pair(it, steps + 1) })
        }

        return checked[end]
    }

    fun part1(lines: List<String>, byteLimit: Int, gridSize: Int): Int {
        val bytes = lines.subList(0, byteLimit)
            .map { b -> b.split(",") }
            .map { (x, y) -> Coord(x.toInt(), y.toInt()) }
            .toSet()
        val grid = gridOfSize(gridSize + 1, gridSize + 1)
        return minSteps(bytes, grid, Coord(0, 0), Coord(gridSize, gridSize))!!
    }

    fun part2(lines: List<String>, minNumBytes: Int, gridSize: Int): String {
        val allbytes = lines.map { b -> b.split(",") }.map { (x, y) -> Coord(x.toInt(), y.toInt()) }
        val grid = gridOfSize(gridSize + 1, gridSize + 1)
        for (numBytes in minNumBytes..lines.size) {
            val bytes = allbytes.subList(0, numBytes).toSet()
            if(minSteps(bytes, grid, Coord(0, 0), Coord(gridSize, gridSize)) == null) {
                return "${bytes.last().x()},${bytes.last().y()}"
            }
        }
        return ""
    }

    val part1Example = part1(exampleInput, 12, 6)
    val part2Example = part2(exampleInput, 12, 6)

    check(part1Example == 22) { -> "Part 1 example failed: Expected 22, received $part1Example" };
    check(part2Example == "6,1") { -> "Part 2 example failed: Expected 6,1, received $part2Example" };

    val timePart1 = measureTime { part1(input, 1024, 70).println() }
    val timePart2 = measureTime { part2(input, 1024, 70).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}