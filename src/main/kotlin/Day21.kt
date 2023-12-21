import java.util.*
import kotlin.math.min
import kotlin.time.measureTime

fun findStart(grid: Grid): Coord {
    val y = grid.indexOfFirst { it.contains('S') }
    val x = grid[y].indexOf('S')
    return Coord(x, y)
}

fun getReachableCoords(grid: Grid, start: Coord, stepLimit: Int): Map<Coord, Int> {
    val seenCoords = mutableMapOf<Coord, Int>()
    val coordsToProcess: Queue<Pair<Coord, Int>> = LinkedList()
    coordsToProcess.add(Pair(start, 0))

    while (coordsToProcess.isNotEmpty()) {
        val (coord, stepsTaken) = coordsToProcess.remove()
        if (seenCoords[coord] != null) {
            continue
        }

        seenCoords[coord] = stepsTaken
        if (stepsTaken < stepLimit) {
            adjacentCoords(grid, coord)
                .filter { grid.get(it) != '#' }
                .forEach { nextCoord ->
                    val currentStepsToNext = seenCoords[nextCoord]
                    if (currentStepsToNext == null || stepsTaken + 1 < currentStepsToNext) {
                        coordsToProcess.add(Pair(nextCoord, stepsTaken + 1))
                    }
                }
        }
    }

    return seenCoords
}

fun main() {
    val exampleLines = readLines("day21-example")
    val lines = readLines("day21")

    fun part1(grid: Grid, stepLimit: Int): Int {
        val start = findStart(grid)
        val reachableCoords = getReachableCoords(grid, start, stepLimit)
        return reachableCoords.values.count { steps -> steps % 2 == stepLimit % 2 }
    }

    fun part2(lines: List<String>): Int {
        return 0
    }

    val part1Example = part1(exampleLines, 6)
    val part2Example = part2(exampleLines)

    check(part1Example == 16) { -> "Part 1 example failed: Expected 16, received $part1Example" };
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    val timePart1 = measureTime { part1(lines, 64).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}