package `2023`

import Coord
import println
import readLines
import java.io.File
import java.lang.Exception
import kotlin.math.max
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

fun getNextCoords(grid: HeatGrid, currentCoord: Coord, currentPath: List<Coord>): List<Coord> {
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

fun getNextCoords2(
    grid: HeatGrid,
    currentCoord: Coord,
    currentStraightPath: List<Coord>
): List<List<Coord>> {
    if (currentCoord == Pair(0, 0) && currentStraightPath.isEmpty()) {
        return listOf(
            listOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(3, 0), Coord(4, 0)),
            listOf(Coord(0, 0), Coord(0, 1), Coord(0, 2), Coord(0, 3), Coord(0, 4)),
        )
    }
    val straight = listOf(
        nextCoordStraight(currentStraightPath.last(), currentCoord),
    )
    val turns = nextCoordsTurn(currentStraightPath.last(), currentCoord).map { c ->
        getStraightPath(currentCoord, c, 4)
    }

    val all = arrayOf(straight, *turns.toTypedArray()).toList()

    return all.filter { nextSteps ->
        val maxX = maxOf(nextSteps.maxOf { it.first }, currentStraightPath.maxOfOrNull { it.first } ?: 0)
        val minX = minOf(nextSteps.minOf { it.first }, currentStraightPath.minOfOrNull { it.first } ?: 0)
        val maxY = maxOf(nextSteps.maxOf { it.second }, currentStraightPath.maxOfOrNull { it.second } ?: 0)
        val minY = minOf(nextSteps.minOf { it.second }, currentStraightPath.minOfOrNull { it.second } ?: 0)

        (maxX - minX < 10) &&
                (maxY - minY < 10) &&
                (nextSteps.all { (x, y) -> x >= 0 && y >= 0 && x < grid.size && y < grid.size })
    }
}

fun getStraightPath(prev: Coord, current: Coord, steps: Int): List<Coord> {
    val diffX = current.first - prev.first
    val diffY = current.second - prev.second
    return (0..<steps).map { i -> Coord(current.first + (i * diffX), current.second + (i * diffY)) }
}

fun hasTurn(coords: Array<Coord>): Boolean {
    val xs = coords.map { it.first }.toSet()
    val ys = coords.map { it.second }.toSet()

    return xs.size > 1 && ys.size > 1
}

fun getAllCoordOptions(size: Int): MutableList<Pair<Coord, List<Coord>>> {
    val coordsToProcess = mutableListOf<Pair<Coord, List<Coord>>>();
    for (x in 0..<size) {
        for (y in 0..<size) {
            listOf(
                Pair(Coord(x, y), listOf(Coord(x - 1, y))),
                Pair(Coord(x, y), listOf(Coord(x + 1, y))),
                Pair(Coord(x, y), listOf(Coord(x, y - 1))),
                Pair(Coord(x, y), listOf(Coord(x, y + 1))),
                Pair(Coord(x, y), listOf(Coord(x - 2, y), Coord(x - 1, y))),
                Pair(Coord(x, y), listOf(Coord(x + 2, y), Coord(x + 1, y))),
                Pair(Coord(x, y), listOf(Coord(x, y - 2), Coord(x, y - 1))),
                Pair(Coord(x, y), listOf(Coord(x, y + 2), Coord(x, y + 1))),
                Pair(Coord(x, y), listOf(Coord(x - 3, y), Coord(x - 2, y), Coord(x - 1, y))),
                Pair(Coord(x, y), listOf(Coord(x + 3, y), Coord(x + 2, y), Coord(x + 1, y))),
                Pair(Coord(x, y), listOf(Coord(x, y - 3), Coord(x, y - 2), Coord(x, y - 1))),
                Pair(Coord(x, y), listOf(Coord(x, y + 3), Coord(x, y + 2), Coord(x, y + 1))),
            ).filterNot { (_, prevCoords) ->
                prevCoords.any { (x, y) -> x < 0 || y < 0 || x >= size || y >= size }
            }.forEach { coordsToProcess.add(it) }
        }
    }
    return coordsToProcess;
}

fun getAllCoordOptions2(size: Int): MutableList<Pair<Coord, List<Coord>>> {
    val coordsToProcess = mutableListOf<Pair<Coord, List<Coord>>>();
    for (x in 0..<size) {
        for (y in 0..<size) {
            (4..<10).flatMap { i ->
                listOf(
                    Pair(Coord(x, y), (1..i).map { j -> Coord(x - j, y) }.reversed()),
                    Pair(Coord(x, y), (1..i).map { j -> Coord(x + j, y) }.reversed()),
                    Pair(Coord(x, y), (1..i).map { j -> Coord(x, y - j) }.reversed()),
                    Pair(Coord(x, y), (1..i).map { j -> Coord(x, y + j) }.reversed()),
                )
            }
                .filterNot { (_, prevCoords) ->
                    prevCoords.any { (x, y) -> x < 0 || y < 0 || x >= size || y >= size }
                }.forEach { coordsToProcess.add(it) }
        }
    }
    return coordsToProcess;
}

fun getNextNode(
    distsByNum: Map<Int, MutableList<Pair<Coord, List<Coord>>>>,
    visited: Set<Pair<Coord, List<Coord>>>,
    highestChecked: Int
): Pair<Pair<Coord, List<Coord>>, Int> {
    distsByNum.keys.filter { int -> int >= highestChecked }.sorted().forEach { int ->
        val result = distsByNum[int]!!.find { !visited.contains(it) }
        if (result != null) {
            return Pair(result, int)
        }
    }
    throw Exception("Failed to find")
}

fun nextCoordStraight(current: Coord, next: Coord): Coord {
    val diffX = next.first - current.first
    val diffY = next.second - current.second
    return Coord(next.first + diffX, next.second + diffY)
}

fun nextCoordsTurn(current: Coord, next: Coord): List<Coord> {
    if (next.first == current.first) {
        return listOf(
            Pair(next.first - 1, next.second),
            Pair(next.first + 1, next.second),
        )
    } else {
        return listOf(
            Pair(next.first, next.second - 1),
            Pair(next.first, next.second + 1),
        )
    }
}

fun dijkstra(grid: HeatGrid, start: Coord, end: Coord): Int {
    val coordsToProcess = getAllCoordOptions(grid.size);
    coordsToProcess.add(Pair(start, listOf()))

    val visited = mutableSetOf<Pair<Coord, List<Coord>>>()

    val dists = mutableMapOf<Coord, MutableMap<List<Coord>, Int>>()
    val distsByNum = mutableMapOf<Int, MutableList<Pair<Coord, List<Coord>>>>()
    for ((coord, arr) in coordsToProcess) {
        dists[coord] = (dists[coord] ?: mutableMapOf())
        dists[coord]!![arr.toList()] = Int.MAX_VALUE

        distsByNum[Int.MAX_VALUE] = (distsByNum[Int.MAX_VALUE] ?: mutableListOf())
        distsByNum[Int.MAX_VALUE]!!.addLast(Pair(coord, arr.toList()))
    }
    dists[start]!![listOf()] = 0
    distsByNum[0] = mutableListOf(Pair(start, listOf()))

    var highestChecked = 0

    while (coordsToProcess.isNotEmpty()) {
        if (coordsToProcess.size % 1000 == 0) {
            coordsToProcess.size.println()
        }
        val (current, num) = getNextNode(distsByNum, visited, highestChecked)
        highestChecked = max(highestChecked, num);
        val (currentCoord, prevStraightPath) = current

        coordsToProcess.remove(current)
        visited.add(current)

        val adjacent = getNextCoords(grid, currentCoord, prevStraightPath)
        for (nextCoord in adjacent) {
            val nextPath = if (hasTurn(arrayOf(*prevStraightPath.toTypedArray(), currentCoord, nextCoord))) {
                listOf(currentCoord)
            } else {
                arrayOf(*prevStraightPath.toTypedArray(), currentCoord).toList()
            }
            val nextTotal = dists[currentCoord]!![prevStraightPath.toList()]!! + grid.get(nextCoord)
            val currentTotal = dists[nextCoord]!![nextPath]!!
            if (nextTotal < currentTotal) {
                dists[nextCoord]!![nextPath] = nextTotal

                distsByNum[nextTotal] = (distsByNum[nextTotal] ?: mutableListOf())
                distsByNum[nextTotal]!!.add(Pair(nextCoord, nextPath))

                distsByNum[currentTotal]!!.remove(Pair(nextCoord, nextPath))
            }
        }
    }

    return dists[end]!!.values.min()
}

fun dijkstra2(grid: HeatGrid, start: Coord, end: Coord): Int {
    val coordsToProcess = getAllCoordOptions2(grid.size);
    coordsToProcess.add(Pair(start, listOf()))

    val visited = mutableSetOf<Pair<Coord, List<Coord>>>()

    val dists = mutableMapOf<Coord, MutableMap<List<Coord>, Int>>()
    val distsByNum = mutableMapOf<Int, MutableList<Pair<Coord, List<Coord>>>>()
    for ((coord, arr) in coordsToProcess) {
        dists[coord] = (dists[coord] ?: mutableMapOf())
        dists[coord]!![arr.toList()] = Int.MAX_VALUE

        distsByNum[Int.MAX_VALUE] = (distsByNum[Int.MAX_VALUE] ?: mutableListOf())
        distsByNum[Int.MAX_VALUE]!!.addLast(Pair(coord, arr.toList()))
    }
    dists[start]!![listOf()] = 0
    distsByNum[0] = mutableListOf(Pair(start, listOf()))

    var highestChecked = 0

    while (coordsToProcess.isNotEmpty()) {
        if (coordsToProcess.size % 1000 == 0) {
            coordsToProcess.size.println()
        }
        val (current, num) = getNextNode(distsByNum, visited, highestChecked)
        highestChecked = max(highestChecked, num);
        val (currentCoord, prevStraightPath) = current

        coordsToProcess.remove(current)
        visited.add(current)

        val nextPaths = getNextCoords2(grid, currentCoord, prevStraightPath)

        for (nextPath in nextPaths) {
            val newPrevPath =
                if (hasTurn(arrayOf(*prevStraightPath.toTypedArray(), currentCoord, *nextPath.toTypedArray()))) {
                    arrayOf(currentCoord, *nextPath.dropLast(1).toTypedArray()).toList()
                } else {
                    if (currentCoord == Pair(0, 0)) {
                        nextPath.dropLast(1)
                    } else {
                        arrayOf(*prevStraightPath.toTypedArray(), currentCoord).toList()
                    }
                }
            val nextTotal = dists[currentCoord]!![prevStraightPath.toList()]!! + nextPath.sumOf { grid.get(it) }
            val nextCoord = nextPath.last()
            val currentTotal = dists[nextCoord]!![newPrevPath]!!
            if (nextTotal < currentTotal) {
                dists[nextCoord]!![newPrevPath] = nextTotal

                distsByNum[nextTotal] = (distsByNum[nextTotal] ?: mutableListOf())
                distsByNum[nextTotal]!!.add(Pair(nextCoord, newPrevPath))

                distsByNum[currentTotal]!!.remove(Pair(nextCoord, newPrevPath))
            }
        }
    }

    File("./debug-day17.txt").writeText(dists.map { it.toString() }.joinToString("\n"))
    return dists[end]!!.values.min() - grid.get(start)
}

fun main() {
    val exampleLines = readLines("2023", "day17-example").map { it.map { c -> c.toString().toInt() } }
    val lines = readLines("2023", "day17").map { it.map { c -> c.toString().toInt() } }

    fun part1(grid: HeatGrid): Int {
        val start = Pair(0, 0)
        val end = Pair(grid.first().size - 1, grid.size - 1)

        return dijkstra(grid, start, end)
    }

    fun part2(grid: HeatGrid): Int {
        val start = Pair(0, 0)
        val end = Pair(grid.first().size - 1, grid.size - 1)

        return dijkstra2(grid, start, end)
    }

    println("example")
//    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

//    check(part1Example == 102) { -> "Part 1 example failed: Expected 102, received $part1Example" };
    check(part2Example == 94) { -> "Part 2 example failed: Expected 94, received $part2Example" };

    println("real")
//    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

//    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}