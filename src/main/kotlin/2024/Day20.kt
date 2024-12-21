package `2024`

import Coord
import Grid
import adjacentCoords
import distance
import findChar
import get
import println
import reachableCoords
import readLines
import java.util.*
import kotlin.time.measureTime

fun main() {
    val exampleInput = readLines("2024", "day20-example")
    val input = readLines("2024", "day20")

    fun race(grid: Grid, start: Coord): Map<Coord, Int> {
        val seen = mutableMapOf<Coord, Int>()
        val q = PriorityQueue<Pair<Coord, Int>> {t1, t2 -> t2.second - t1.second}
        q.add(Pair(start, 0))

        while (q.isNotEmpty()) {
            val (coord, steps) = q.remove()
            if (seen.containsKey(coord)) continue
            seen[coord] = steps

            grid.adjacentCoords(coord).filter { grid.get(it) != '#' }.forEach { q.add(Pair(it, steps + 1)) }
        }

        return seen
    }

    fun numCheats(grid: Grid, timeToSave: Int, maxCheatDistance: Int): Int {
        val start = grid.findChar('S')
        val distances = race(grid, start)
        val cheats = mutableMapOf<Pair<Coord, Coord>, Int>()

        distances.forEach { (coord, distance) ->
            val reachableCoords = grid.reachableCoords(coord, maxCheatDistance).filter { grid.get(it) != '#' }
            reachableCoords.forEach { c ->
                val cheatDistance = coord.distance(c)
                val timeSaved = distances[c]!! - (distance + cheatDistance)
                if (timeSaved > 0) {
                    cheats[Pair(coord, c)] = timeSaved
                }
            }
        }

        return cheats.count { (_, timeSaved) -> timeSaved >= timeToSave }
    }

    fun part1(grid: Grid, timeToSave: Int): Int = numCheats(grid, timeToSave, 2)
    fun part2(grid: Grid, timeToSave: Int): Int = numCheats(grid, timeToSave, 20)

    val part1Example = part1(exampleInput, 2)
    val part2Example = part2(exampleInput, 50)

    check(part1Example == 44) { -> "Part 1 example failed: Expected 44, received $part1Example" };
    check(part2Example == 285) { -> "Part 2 example failed: Expected 285, received $part2Example" };

    val timePart1 = measureTime { part1(input, 100).println() }
    val timePart2 = measureTime { part2(input, 100).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}