import java.util.*
import kotlin.time.measureTime

typealias Path = List<Coord>

fun mazeStart(grid: Grid): Coord {
    return Coord(grid[0].indexOf('.'), 0)
}

fun mazeEnd(grid: Grid): Coord {
    return Coord(grid.last().indexOf('.'), grid.size - 1)
}

fun validNextStepsWithSlopes(grid: Grid, coord: Coord): List<Coord> {
    val (x, y) = coord
    return listOfNotNull(
        if (y > 0 && listOf('^', '.').contains(grid.get(x, y - 1))) Coord(x, y - 1) else null,
        if (y < grid.size - 1 && listOf('v', '.').contains(grid.get(x, y + 1))) Coord(x, y + 1) else null,
        if (x > 0 && listOf('<', '.').contains(grid.get(x - 1, y))) Coord(x - 1, y) else null,
        if (x < grid[0].length - 1 && listOf('>', '.').contains(grid.get(x + 1, y))) Coord(x + 1, y) else null,
    )
}

fun validNestStepsNoSlopes(grid: Grid, coord: Coord): List<Coord> {
    return adjacentCoords(grid, coord).filter { grid.get(it) != '#' }
}

fun longestHike(grid: Grid, getValidSteps: (grid: Grid, coord: Coord) -> List<Coord>): Int {
    val start = mazeStart(grid)
    val end = mazeEnd(grid)
    val validHikes = mutableListOf<Path>()
    val pathsToProcess: Queue<Path> = LinkedList()
    pathsToProcess.add(listOf(start))

    while (pathsToProcess.isNotEmpty()) {
        val path = pathsToProcess.remove()
        val currentCoord = path.last()
        if (currentCoord == end) {
            validHikes.add(path)
            continue
        }

        getValidSteps(grid, currentCoord)
            .filter { !path.contains(it) }
            .forEach { nextCoord ->
                val newPath = path.toMutableList()
                newPath.addLast(nextCoord)
                pathsToProcess.add(newPath)
            }
    }

    return validHikes.maxOf { it.size } - 1 // #steps = (#tiles - 1)
}

fun main() {
    val exampleLines = readLines("day23-example")
    val lines = readLines("day23")

    fun part1(grid: Grid): Int {
        return longestHike(grid, ::validNextStepsWithSlopes)
    }

    fun part2(grid: Grid): Int {
        return longestHike(grid, ::validNestStepsNoSlopes)
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 94) { -> "Part 1 example failed: Expected 94, received $part1Example" };
    check(part2Example == 154) { -> "Part 2 example failed: Expected 154, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}