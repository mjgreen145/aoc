import kotlin.math.abs
import kotlin.time.measureTime

fun getNextCoord(current: Coord, dir: String, distance: Int): Coord {
    return when (dir) {
        "U" -> Coord(current.first, current.second - distance)
        "D" -> Coord(current.first, current.second + distance)
        "L" -> Coord(current.first - distance, current.second)
        "R" -> Coord(current.first + distance, current.second)
        else -> throw Exception("Unknown direction $dir")
    }
}

fun internalArea(pathVertices: List<Coord>, pathLength: Long): Long {
    val area = abs(pathVertices.indices.fold(0) { acc, i ->
        val p1 = pathVertices[i]
        val p2 = if (i + 1 == pathVertices.size) pathVertices[0] else pathVertices[i + 1]

        acc + ((p1.x() * p2.y()) - (p1.y() * p2.x()))
    }) / 2 // Gauss
    return area - (pathLength / 2) + 1 // Pick theorem
}

fun getTotalArea(instructions: List<Pair<String, Int>>): Long {
    var current = Coord(0, 0)
    val path = mutableListOf<Coord>()
    var pathLength = 0L

    instructions.forEach { (dir, distance) ->
        val nextCoord = getNextCoord(current, dir, distance)
        path.addLast(nextCoord)
        pathLength += distance.toLong()
        current = nextCoord
    }

    return internalArea(path, pathLength) + pathLength
}

fun main() {
    val exampleLines = readLines("day18-example")
    val lines = readLines("day18")

    fun part1(lines: List<String>): Long {
        val instructions = lines.map { line ->
            val (dir, distance) = line.split(" ")
            Pair(dir, distance.toInt())
        }

        return getTotalArea(instructions)
    }

    fun part2(lines: List<String>): Int {
        return 0
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 62L) { -> "Part 1 example failed: Expected 62, received $part1Example" };
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}