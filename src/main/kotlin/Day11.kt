import kotlin.math.absoluteValue
import kotlin.time.measureTime

fun getRowsToExpand(lines: List<String>): Set<Int> {
    return lines.mapIndexedNotNull { index, line ->
        if (line.all { it == '.' }) index else null
    }.toSet()
}

fun getColsToExpand(lines: List<String>): Set<Int> {
    return lines.first()
        .mapIndexedNotNull { i, _ ->
            val expand = lines.map { it[i] }.all { it == '.' }
            if (expand) i else null
        }
        .toSet()
}

typealias Galaxy = Pair<Int, Int>

fun getGalaxies(map: List<String>): List<Galaxy> {
    val galaxies = mutableListOf<Galaxy>()
    map.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            if (char == '#') {
                galaxies.add(Galaxy(x, y))
            }
        }
    }
    return galaxies
}

fun distanceBetween(
    a: Galaxy,
    b: Galaxy,
    rowsToExpand: Set<Int>,
    colsToExpand: Set<Int>,
    distanceToExpand: Long
): Long {
    val (ax, ay) = a;
    val (bx, by) = b;
    val colRange = if (ax < bx) IntRange(ax, bx) else IntRange(bx, ax)
    val rowRange = if (ay < by) IntRange(ay, by) else IntRange(by, ay)

    val expandedRowsBetween = rowsToExpand.filter { i -> rowRange.contains(i) }.size
    val expandedColsBetween = colsToExpand.filter { i -> colRange.contains(i) }.size

    val normalDistance = (a.first - b.first).absoluteValue + (a.second - b.second).absoluteValue
    return normalDistance + ((distanceToExpand - 1) * (expandedRowsBetween + expandedColsBetween))
}

fun sumDistances(lines: List<String>, distanceToExpand: Long): Long {
    val rowsToExpand = getRowsToExpand(lines);
    val colsToExpand = getColsToExpand(lines);
    val galaxies = getGalaxies(lines)
    return galaxies.mapIndexed { i, g ->
        galaxies.drop(i + 1).sumOf { other -> distanceBetween(g, other, rowsToExpand, colsToExpand, distanceToExpand) }
    }.sum()
}

fun main() {
    val exampleLines = readLines("day11-example")
    val lines = readLines("day11")

    fun part1(lines: List<String>): Long {
        return sumDistances(lines, 2)
    }

    fun part2(lines: List<String>): Long {
        return sumDistances(lines, 1000000)
    }

    val part1Example = part1(exampleLines)
    check(part1Example == 374L) { -> "Part 1 example failed: Expected 374, received $part1Example" };

    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")

    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")
}