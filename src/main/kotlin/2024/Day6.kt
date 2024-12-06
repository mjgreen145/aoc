package `2024`

import Dir
import Coord
import Grid
import allCoords
import containsCoord
import findChar
import get
import getOrEmpty
import move
import println
import readLines
import turn
import kotlin.time.measureTime

fun loops(grid: Grid, obstruction: Coord): Boolean {
    val start = grid.findChar('^');
    val seenCoords = mutableSetOf<Pair<Coord, Dir>>()
    var pos = start
    var dir = Dir.North
    while (grid.containsCoord(pos)) {
        if (seenCoords.contains(Pair(pos, dir))) {
            return true;
        }
        seenCoords.add(Pair(pos, dir))
        while (pos.move(dir, 1) == obstruction || grid.getOrEmpty(pos.move(dir, 1)) == "#") {
            dir = turn(dir, Turn.Right)
        }
        seenCoords.add(Pair(pos, dir))
        pos = pos.move(dir, 1)
    }
    return false
}

fun main() {
    val exampleLines = readLines("2024", "day6-example")
    val lines = readLines("2024", "day6")

    fun part1(grid: Grid): Int {
        val start = grid.findChar('^');
        val seenCoords = mutableSetOf<Coord>()
        var pos = start
        var dir = Dir.North
        while (grid.containsCoord(pos)) {
            seenCoords.add(pos)
            while (grid.getOrEmpty(pos.move(dir, 1)) == "#") {
                dir = turn(dir, Turn.Right)
            }
            pos = pos.move(dir, 1)
        }
        return seenCoords.size
    }

    fun part2(grid: Grid): Int {
        return grid.allCoords().filter { grid.get(it) == '.' }.count { c -> loops(grid, c) }
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 41) { -> "Part 1 example failed: Expected 41, received $part1Example" };
    check(part2Example == 6) { -> "Part 2 example failed: Expected 6, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}