import kotlin.time.measureTime

typealias HeatGrid = List<List<Int>>

fun HeatGrid.get(x: Int, y: Int): Int = this[y][x]
fun HeatGrid.get(c: Coord): Int = this[c.second][c.first]

fun adjacentCoords(grid: HeatGrid, currentCoord: Coord): List<Coord> {
    val (x, y) = currentCoord
    return listOfNotNull(
        if (y > 0) Pair(x, y - 1) else null,
        if (y < grid.size - 1) Pair(x, y + 1) else null,
        if (x > 0) Pair(x - 1, y) else null,
        if (x < grid[0].size - 1) Pair(x + 1, y) else null,
    )
}

fun getNextCoords(grid: HeatGrid, currentCoord: Coord, currentPath: Array<Coord>): List<Coord> {
    val last4 = currentPath.take(4)
    val nextOptions = adjacentCoords(grid, currentCoord)

    return nextOptions.filter { coord ->
        val maxX = maxOf(coord.first, last4.maxOfOrNull { it.first } ?: 0)
        val minX = minOf(coord.first, last4.minOfOrNull { it.first } ?: 0)
        val maxY = maxOf(coord.second, last4.maxOfOrNull { it.second } ?: 0)
        val minY = minOf(coord.second, last4.minOfOrNull { it.second } ?: 0)

        (maxX - minX <= 3) && (maxY - minY <= 3) && (!currentPath.contains(coord))
    }
}

fun hasTurn(coords: Array<Coord>): Boolean {
    val xs = coords.map { it.first }.toSet()
    val ys = coords.map { it.second }.toSet()

    return xs.size > 1 && ys.size > 1
}

fun getAllCoordOptions(size: Int): MutableList<Pair<Coord, Array<Coord>>> {
    val coordsToProcess = mutableListOf<Pair<Coord, Array<Coord>>>();
    for (x in 0..<size) {
        for (y in 0..<size) {
            listOf(
                Pair(Coord(x, y), arrayOf(Coord(x - 1, y))),
                Pair(Coord(x, y), arrayOf(Coord(x + 1, y))),
                Pair(Coord(x, y), arrayOf(Coord(x, y - 1))),
                Pair(Coord(x, y), arrayOf(Coord(x, y + 1))),
                Pair(Coord(x, y), arrayOf(Coord(x - 2, y), Coord(x - 1, y))),
                Pair(Coord(x, y), arrayOf(Coord(x + 2, y), Coord(x + 1, y))),
                Pair(Coord(x, y), arrayOf(Coord(x, y - 2), Coord(x, y - 1))),
                Pair(Coord(x, y), arrayOf(Coord(x, y + 2), Coord(x, y + 1))),
                Pair(Coord(x, y), arrayOf(Coord(x - 3, y), Coord(x - 2, y), Coord(x - 1, y))),
                Pair(Coord(x, y), arrayOf(Coord(x + 3, y), Coord(x + 2, y), Coord(x + 1, y))),
                Pair(Coord(x, y), arrayOf(Coord(x, y - 3), Coord(x, y - 2), Coord(x, y - 1))),
                Pair(Coord(x, y), arrayOf(Coord(x, y + 3), Coord(x, y + 2), Coord(x, y + 1))),
            ).filterNot { (_, prevCoords) ->
                prevCoords.any { (x, y) -> x < 0 || y < 0 || x >= size || y >= size }
            }.forEach { coordsToProcess.add(it) }
        }
    }
    return coordsToProcess;
}

fun dijkstra(grid: HeatGrid, start: Coord, end: Coord): Int {
    val coordsToProcess = getAllCoordOptions(grid.size);
    coordsToProcess.add(Pair(start, arrayOf()))

    val visited = mutableSetOf<Pair<Coord, Array<Coord>>>()

    val dists = mutableMapOf<Coord, MutableMap<List<Coord>, Int>>()
    for ((coord, arr) in coordsToProcess) {
        dists[coord] = (dists[coord] ?: mutableMapOf())
        dists[coord]!![arr.toList()] = Int.MAX_VALUE
    }
    dists[start]!![listOf()] = 0

    while (coordsToProcess.isNotEmpty()) {
        if (coordsToProcess.size % 1000 == 0) {
            coordsToProcess.size.println()
        }
        val current =
            coordsToProcess.filter { !visited.contains(it) }.sortedBy { (c, arr) -> dists[c]!![arr.toList()] }.first()
        val (currentCoord, prevStraightPath) = current
        coordsToProcess.remove(current)
        visited.add(current)

        val adjacent = getNextCoords(grid, currentCoord, prevStraightPath)
        for (nextCoord in adjacent) {
            val nextPath = if (hasTurn(arrayOf(*prevStraightPath, currentCoord, nextCoord))) {
                listOf(currentCoord)
            } else {
                arrayOf(*prevStraightPath, currentCoord).toList()
            }
            if (dists[currentCoord]!![prevStraightPath.toList()]!! + grid.get(nextCoord) < dists[nextCoord]!![nextPath]!!) {
                dists[nextCoord]!![nextPath] = dists[currentCoord]!![prevStraightPath.toList()]!! + grid.get(nextCoord)
            }
        }
    }

    return dists[end]!!.values.min()
}

fun main() {
    val exampleLines = readLines("day17-example").map { it.map { c -> c.toString().toInt() } }
    val lines = readLines("day17").map { it.map { c -> c.toString().toInt() } }

    fun part1(grid: HeatGrid): Int {
        val start = Pair(0, 0)
        val end = Pair(grid.first().size - 1, grid.size - 1)

        return dijkstra(grid, start, end)
    }

    fun part2(grid: HeatGrid): Int {
        return 0
    }

    println("example")
    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 102) { -> "Part 1 example failed: Expected 102, received $part1Example" };
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    println("real")
    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}