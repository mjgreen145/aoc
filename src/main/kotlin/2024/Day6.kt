package `2024`

import Coord
import Dir
import Grid
import Turn
import containsCoord
import findChar
import getOrEmpty
import move
import println
import readLines
import turn
import kotlin.time.measureTime

fun walk(grid: Grid, obstruction: Coord?): Pair<Set<Coord>, Boolean> {
    val start = grid.findChar('^');
    var pos = start
    var dir = Dir.North
    val seenCoords = mutableSetOf<Pair<Coord, Dir>>()
    var looped = false
    while (true) {
        if (seenCoords.contains(Pair(pos, dir))) {
            looped = true
            break
        }
        if (!grid.containsCoord(pos)) break

        seenCoords.add(Pair(pos, dir))

        val next = pos.move(dir, 1)
        if (next == obstruction || grid.getOrEmpty(next) == "#") {
            dir = turn(dir, Turn.Right)
        } else {
            pos = next
        }
    }
    return Pair(seenCoords.map { (coord) -> coord }.toSet(), looped)
}

fun main() {
    val exampleLines = readLines("2024", "day6-example")
    val lines = readLines("2024", "day6")

    fun part1(grid: Grid): Int {
        return walk(grid, null).first.size
    }

    fun part2(grid: Grid): Int {
        val start = grid.findChar('^');
        val normalPath = walk(grid, null).first
        return (normalPath - start).count { c -> walk(grid, c).second }
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