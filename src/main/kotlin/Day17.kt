import kotlin.time.measureTime

typealias HeatGrid = List<List<Int>>

fun HeatGrid.get(x: Int, y: Int): Int = this[y][x]
fun HeatGrid.get(c: Coord): Int = this[c.second][c.first]

fun getNextCoords(grid: HeatGrid, currentCoord: Coord, currentPath: Array<Coord>): List<Coord> {
    val (x, y) = currentCoord
    val last4 = currentPath.take(4)
    val nextOptions = listOfNotNull(
        if (y > 0) Pair(x, y - 1) else null,
        if (y < grid.size - 1) Pair(x, y + 1) else null,
        if (x > 0) Pair(x - 1, y) else null,
        if (x < grid[0].size - 1) Pair(x + 1, y) else null,
    )


    val legal = nextOptions.filter { coord ->
        val maxX = maxOf(coord.first, last4.maxOf { it.first })
        val minX = minOf(coord.first, last4.minOf { it.first })
        val maxY = maxOf(coord.second, last4.maxOf { it.second })
        val minY = minOf(coord.second, last4.minOf { it.second })

        (maxX - minX <= 3) && (maxY - minY <= 3) && (last4.size <= 1 || coord != last4[1])
    }

    return legal
}

fun walk(
    heatGrid: HeatGrid,
    current: Coord,
    fastestRoutes: MutableMap<Coord, Pair<Array<Coord>, Int>>,
    currentTotal: Int = 0,
    currentPath: Array<Coord> = arrayOf(current)
) {
    fastestRoutes[current] = Pair(currentPath, currentTotal)

    val nextCoords = getNextCoords(heatGrid, current, currentPath)

    for (nextCoord in nextCoords) {
        val fastestRoute = fastestRoutes[nextCoord]
        if (fastestRoute == null || fastestRoute.second > currentTotal + heatGrid.get(current)) {
            walk(
                heatGrid,
                nextCoord,
                fastestRoutes,
                currentTotal + heatGrid.get(current),
                arrayOf(nextCoord, *currentPath)
            )
        }
    }
}

fun getBestPath(
    start: Coord,
    end: Coord,
    heatGrid: HeatGrid,
): Pair<Array<Coord>, Int> {
    val fastestRoutes = mutableMapOf<Coord, Pair<Array<Coord>, Int>>()
    walk(heatGrid, end, fastestRoutes)
    writeDebugDay17(heatGrid, fastestRoutes)

    return fastestRoutes[start]!!
}

fun main() {
    val exampleLines = readLines("day17-example").map { it.map { c -> c.toString().toInt() } }
    val lines = readLines("day17").map { it.map { c -> c.toString().toInt() } }

    fun part1(grid: HeatGrid): Int {
        val start = Pair(0, 0)
        val end = Pair(grid.first().size - 1, grid.size - 1)
        val (bestPath, total) = getBestPath(start, end, grid)

        bestPath.toList().println()

        return total
    }

    fun part2(grid: HeatGrid): Int {
        return 0
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 102) { -> "Part 1 example failed: Expected 102, received $part1Example" };
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}