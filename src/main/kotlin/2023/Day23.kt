package `2023`

import Coord
import Grid
import adjacentCoords
import get
import println
import readLines
import kotlin.time.measureTime

typealias Path = MutableList<Coord>

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
    return grid.adjacentCoords(coord).filter { grid.get(it) != '#' }
}

typealias Graph = Map<Coord, Set<Pair<Coord, Int>>>

fun buildMazeGraph(grid: Grid, getValidSteps: (grid: Grid, coord: Coord) -> List<Coord>): Graph {
    val start = mazeStart(grid)
    val end = mazeEnd(grid)

    tailrec fun findNextNode(path: Path): Pair<Coord, Int> {
        val current = path.last()
        val options = getValidSteps(grid, current).filter { !path.contains(it) }
        if (current == end || current == start || options.size > 1) {
            return Pair(current, path.size - 1)
        }
        path.addLast(options.first())
        return findNextNode(path)
    }

    val graph = mutableMapOf<Coord, MutableSet<Pair<Coord, Int>>>()

    val nodesToProcess = mutableListOf(start)

    while (nodesToProcess.isNotEmpty()) {
        val node = nodesToProcess.removeFirst()
        graph[node] = mutableSetOf()
        val options = getValidSteps(grid, node)
        for (option in options) {
            val (nextNode, distance) = findNextNode(mutableListOf(node, option))
            graph[node]!!.add(Pair(nextNode, distance))
            if (graph[nextNode] == null && nextNode != end) {
                nodesToProcess.add(nextNode)
            }
        }
    }

    return graph
}

fun getAllPaths(graph: Graph, start: Coord, end: Coord): List<Pair<Path, Int>> {
    val completePaths = mutableListOf<Pair<Path, Int>>()
    val pathsToProcess = mutableListOf(Pair(mutableListOf(start), 0))

    while (pathsToProcess.isNotEmpty()) {
        val (path, distance) = pathsToProcess.removeFirst()
        if (path.last() == end) {
            completePaths.add(Pair(path, distance))
            continue
        }

        graph[path.last()]!!
            .filter { (node, _) -> !path.contains(node) }
            .forEach { (node, dist) ->
                val newPath = path.toMutableList()
                newPath.addLast(node)
                pathsToProcess.add(Pair(newPath, distance + dist))
            }
    }

    return completePaths
}

fun main() {
    val exampleLines = readLines("2023", "day23-example")
    val lines = readLines("2023", "day23")

    fun part1(grid: Grid): Int {
        val graph = buildMazeGraph(grid, ::validNextStepsWithSlopes)

        val allPaths = getAllPaths(graph, mazeStart(grid), mazeEnd(grid))
        return allPaths.maxOf { (_, dist) -> dist }
    }

    fun part2(grid: Grid): Int {
        val graph = buildMazeGraph(grid, ::validNestStepsNoSlopes)

        val start = mazeStart(grid)
        val end = mazeEnd(grid)
        fun coordLabel(coord: Coord): String {
            if (coord == start) return "start"
            if (coord == end) return "end"
            return "\"${coord.first},${coord.second}\""
        }
        val processed = mutableSetOf<Pair<Coord, Coord>>()
        graph.entries.flatMap { (node, dests) ->
            dests.mapNotNull { (destNode, dist) ->
                if (!processed.contains(Pair(destNode, node))) {
                    processed.add(Pair(node, destNode))
                    "${coordLabel(node)} -- ${coordLabel(destNode)}"
                } else null
            }
        }.joinToString("\n").println()

//        val allPaths = getAllPaths(graph, mazeStart(grid), mazeEnd(grid))
//        return allPaths.maxOf { (_, dist) -> dist }
        return 0
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 94) { -> "Part 1 example failed: Expected 94, received $part1Example" };
//    check(part2Example == 154) { -> "Part 2 example failed: Expected 154, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")

    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")

    // 4890 too low
}