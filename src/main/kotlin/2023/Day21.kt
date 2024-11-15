package `2023`

import Coord
import Grid
import adjacentCoords
import get
import println
import readLines
import java.util.*
import kotlin.time.measureTime

fun findStart(grid: Grid): Coord {
    val y = grid.indexOfFirst { it.contains('S') }
    val x = grid[y].indexOf('S')
    return Coord(x, y)
}

fun getReachableCoords(grid: Grid, start: Coord): Map<Coord, Int> {
    val seenCoords = mutableMapOf<Coord, Int>()
    val coordsToProcess: Queue<Pair<Coord, Int>> = LinkedList()
    coordsToProcess.add(Pair(start, 0))

    while (coordsToProcess.isNotEmpty()) {
        val (coord, stepsTaken) = coordsToProcess.remove()
        if (seenCoords[coord] != null) {
            continue
        }

        seenCoords[coord] = stepsTaken
        adjacentCoords(grid, coord)
            .filter { grid.get(it) != '#' }
            .forEach { nextCoord ->
                val currentStepsToNext = seenCoords[nextCoord]
                if (currentStepsToNext == null || stepsTaken + 1 < currentStepsToNext) {
                    coordsToProcess.add(Pair(nextCoord, stepsTaken + 1))
                }
            }
    }

    return seenCoords
}

fun sumIntsTo(int: Long): Long {
    return int * (int + 1) / 2
}

fun sumEvensTo(int: Long): Long {
    val numsToSum = int / 2
    return numsToSum * (numsToSum + 1)
}

fun sumOddsTo(int: Long): Long {
    val numsToSum = int / 2
    return numsToSum * numsToSum
}

fun getOffsetGrids(grid: Grid): List<Grid> {
    val start = findStart(grid)
    val (x, y) = start
    val topRight = (
            grid.subList(y + 1, grid.size).map { line -> line.substring(x) + line.substring(0, x) }.toTypedArray() +
                    grid.subList(0, y + 1).map { line -> line.substring(x) + line.substring(0, x) }.toTypedArray()
            )

    val topLeft = (
            grid.subList(y + 1, grid.size).map { line -> line.substring(x + 1) + line.substring(0, x + 1) }
                .toTypedArray() +
                    grid.subList(0, y + 1).map { line -> line.substring(x + 1) + line.substring(0, x + 1) }
                        .toTypedArray()
            )

    val bottomLeft = (
            grid.subList(y, grid.size).map { line -> line.substring(x + 1) + line.substring(0, x + 1) }
                .toTypedArray() +
                    grid.subList(0, y).map { line -> line.substring(x + 1) + line.substring(0, x + 1) }
                        .toTypedArray()
            )

    val bottomRight = (
            grid.subList(y, grid.size).map { line -> line.substring(x) + line.substring(0, x) }.toTypedArray() +
                    grid.subList(0, y).map { line -> line.substring(x) + line.substring(0, x) }.toTypedArray()
            )

    return listOf(topLeft, topRight, bottomLeft, bottomRight).map { it.toList() }
}

fun main() {
    val exampleLines = readLines("2023", "day21-example")
    val lines = readLines("2023", "day21")

    fun part1(grid: Grid, stepLimit: Int): Int {
        val start = findStart(grid)
        val reachableCoords = getReachableCoords(grid, start)
        return reachableCoords.values.count { steps -> steps <= stepLimit && steps % 2 == stepLimit % 2 }
    }

    fun part2(grid: Grid, stepLimit: Long): Long {
        val gridSize = grid.size
        val numFullSidesWalkable = stepLimit / gridSize
        val remainderSteps = stepLimit % gridSize

        val totalReachable = getOffsetGrids(grid).sumOf { offsetGrid ->
            val start = findStart(offsetGrid)
            val allCoords = getReachableCoords(offsetGrid, start)
            allCoords.values.max().println()
            val reachableFullOdd = allCoords.values.count { steps -> steps % 2 == 1 }
            val reachableFullEven = allCoords.values.count { steps -> steps % 2 == 0 }
            val reachableRemainder1 =
                allCoords.values.count { steps -> steps <= (remainderSteps + gridSize) && steps % 2 == 0 }
            val reachableRemainder2 = allCoords.values.count { steps -> steps <= remainderSteps && steps % 2 == 1 }

            listOf(
                reachableFullOdd * sumOddsTo(numFullSidesWalkable - 1),
                reachableFullEven * sumEvensTo(numFullSidesWalkable - 1),
                numFullSidesWalkable * reachableRemainder1,
                (numFullSidesWalkable + 1) * reachableRemainder2
            ).sum()
        }

        // Account for double counting sides
        return totalReachable - (stepLimit * 4)
    }

    val part1Example = part1(exampleLines, 6)

    check(part1Example == 16) { -> "Part 1 example failed: Expected 16, received $part1Example" };

    val timePart1 = measureTime { part1(lines, 64).println() }
    val timePart2 = measureTime { part2(lines, 26501365L).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")

    // 630165203396840 too high
    // 619380710900756 nope
    // 619360275262605 nope
    // 616665249602669 nope
    // 616659910294840 too low
    // 608638607266640 too low
}