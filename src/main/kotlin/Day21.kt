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

fun main() {
    val exampleLines = readLines("day21-example")
    val lines = readLines("day21")

    fun part1(grid: Grid, stepLimit: Int): Int {
        val start = findStart(grid)
        val reachableCoords = getReachableCoords(grid, start)
        return reachableCoords.values.count { steps -> steps <= stepLimit && steps % 2 == stepLimit % 2 }
    }

    fun part2(grid: Grid, stepLimit: Long): Long {
        val gridSize = grid.size
        val (x, y) = findStart(grid)
        val offsetGrid = (
                grid.subList(y, gridSize).map { line -> line.substring(x) + line.substring(0, x) }.toTypedArray() +
                        grid.subList(0, y).map { line -> line.substring(x) + line.substring(0, x) }
                            .toTypedArray()
                ).toList()

        val corners = listOf(
            Coord(0, 0),
            Coord(0, offsetGrid.size - 1),
            Coord(offsetGrid[0].length - 1, 0),
            Coord(offsetGrid[0].length - 1, offsetGrid.size - 1)
        )

        val numFullSidesWalkable = stepLimit / gridSize
        val remainderSteps = stepLimit % gridSize

        val totalReachable = corners.sumOf { corner ->
            val allCoords = getReachableCoords(offsetGrid, corner)
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
    // 619360275262605 nope
    // 616665249602669 nope
    // 616659910294840 too low
    // 608638607266640 too low
}