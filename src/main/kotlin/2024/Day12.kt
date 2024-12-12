package `2024`

import Coord
import Dir
import Grid
import adjacent
import adjacentCoords
import allCoords
import containsCoord
import get
import getOrEmpty
import move
import println
import readLines
import turn
import kotlin.time.measureTime

fun main() {
    val exampleInput = readLines("2024", "day12-example")
    val input = readLines("2024", "day12")

    fun coordsInRegion(coord: Coord, grid: Grid, seen: MutableSet<Coord>): Set<Coord> {
        val adjacent = grid.adjacentCoords(coord).filter { grid.get(it) == grid.get(coord) && !seen.contains(it) }
        if (adjacent.isEmpty()) return seen

        seen.addAll(adjacent)
        return adjacent.flatMap { coordsInRegion(it, grid, seen) }.toSet()
    }

    fun getRegions(grid: Grid): List<Set<Coord>> {
        val checked = mutableSetOf<Coord>()
        return grid.allCoords().mapNotNull { coord ->
            if (!checked.contains(coord)) coordsInRegion(
                coord,
                grid,
                mutableSetOf(coord)
            ).also { checked.addAll(it) } else null
        }
    }

    fun area(region: Set<Coord>): Int = region.size

    fun perimeter(region: Set<Coord>, grid: Grid): Int {
        return region.sumOf { it.adjacent().count { c -> grid.getOrEmpty(c) != grid.get(it).toString() } }
    }

    fun walkCheckingLeft(grid: Grid, pos: Coord, dir: Dir, value: String, walked: MutableSet<Pair<Coord, Dir>>, turns: Int = 0): Int {
        if (walked.contains(Pair(pos, dir))) { return turns }

        val posToLeft = pos.move(turn(dir, Turn.Left))
        val canTurnLeft = grid.containsCoord(posToLeft) && grid.getOrEmpty(posToLeft) == value
        val mustTurnRight = grid.getOrEmpty(pos.move(dir)) != value
        walked.add(Pair(pos, dir))

        return when {
            canTurnLeft -> walkCheckingLeft(grid, posToLeft, turn(dir, Turn.Left), value, walked, turns + 1)
            mustTurnRight -> walkCheckingLeft(grid, pos, turn(dir, Turn.Right), value, walked, turns + 1)
            else -> walkCheckingLeft(grid, pos.move(dir), dir, value, walked, turns)
        }
    }

    fun sides(region: Set<Coord>, grid: Grid): Int {
        val regionValue = grid.get(region.first()).toString()
        val starts = region.filter { grid.get(it).toString() != grid.getOrEmpty(it.move(Dir.North)) }
        val checked = mutableSetOf<Pair<Coord, Dir>>()
        return starts.sumOf { start ->
            walkCheckingLeft(grid, start, Dir.East, regionValue, checked)
        }
    }

    fun part1(grid: Grid): Int {
        return getRegions(grid).sumOf { r -> area(r) * perimeter(r, grid) }
    }

    fun part2(grid: Grid): Int {
        return getRegions(grid).sumOf { r -> area(r) * sides(r, grid) }
    }

    val part1Example = part1(exampleInput)
    val part2Example = part2(exampleInput)

    check(part1Example == 1930) { -> "Part 1 example failed: Expected 1930, received $part1Example" };
    check(part2Example == 1206) { -> "Part 2 example failed: Expected 1206, received $part2Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}