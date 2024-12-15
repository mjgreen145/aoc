package `2024`

import Coord
import Dir
import Grid
import allCoords
import findChar
import get
import move
import println
import readInput
import kotlin.time.measureTime

var moveToDir = mapOf(
    '^' to Dir.North,
    'v' to Dir.South,
    '>' to Dir.East,
    '<' to Dir.West,
)

typealias BigBox = Pair<Coord, Coord>

fun main() {
    val exampleInput1 = readInput("2024", "day15-example1")
    val exampleInput2 = readInput("2024", "day15-example2")
    val input = readInput("2024", "day15")

    fun part1(input: String): Int {
        val grid = input.substringBefore("\n\n").split("\n")
        val walls = grid.allCoords().filter { grid.get(it) == '#' }.toSet()
        val boxes = grid.allCoords().filter { grid.get(it) == 'O' }.toMutableSet()
        var robot = grid.findChar('@')
        val moves = input.substringAfter("\n\n").filter { it != '\n' }.map { moveToDir[it]!! }

        for (dir in moves) {
            val nextRobot = robot.move(dir)
            var coord = nextRobot
            while (boxes.contains(coord)) {
                coord = coord.move(dir)
            }
            if (walls.contains(coord)) continue
            boxes.add(coord)
            boxes.remove(nextRobot)
            robot = nextRobot
        }

        return boxes.sumOf { (x, y) -> x + 100 * y }
    }

    fun transform(grid: Grid): Grid {
        return grid.map { line ->
            line.map { c ->
                when (c) {
                    '#' -> "##"
                    'O' -> "[]"
                    '.' -> ".."
                    '@' -> "@."
                    else -> c
                }
            }.joinToString("")
        }
    }

    fun getImpactedBoxes(boxes: Set<BigBox>, robot: Coord, dir: Dir): MutableSet<BigBox> {
        val coord = robot.move(dir)
        val box = boxes.find { (b1, b2) -> b1 == coord || b2 == coord } ?: return emptySet<BigBox>().toMutableSet()

        return if (dir in listOf(Dir.East, Dir.West)) {
            getImpactedBoxes(boxes, robot.move(dir, 2), dir).also { it.add(box) }
        } else {
            val impacted = mutableSetOf(box)
            impacted.addAll(getImpactedBoxes(boxes, box.first, dir))
            impacted.addAll(getImpactedBoxes(boxes, box.second, dir))
            impacted
        }
    }

    fun printGrid(boxes: Set<BigBox>, walls: Set<Coord>, robot: Coord, grid: Grid) {
        grid.mapIndexed { y, _ ->
            grid[y].mapIndexed { x, _ ->
                val box = boxes.find { (b1, b2) -> Pair(x, y) == b1 || Pair(x, y) == b2 }
                if (Pair(x, y) == robot) '@'
                else if (walls.contains(Pair(x, y))) '#'
                else if (box != null) { if (Pair(x, y) == box.first) '[' else ']' }
                else '.'
            }.joinToString("")
        }.joinToString("\n").println()
    }

    fun part2(input: String): Int {
        val grid = transform(input.substringBefore("\n\n").split("\n"))
        val walls = grid.allCoords().filter { grid.get(it) == '#' }.toSet()
        val boxes =
            grid.allCoords().filter { grid.get(it) == '[' }.map { c -> Pair(c, c.move(Dir.East)) }.toMutableSet()
        var robot = grid.findChar('@')
        val moves = input.substringAfter("\n\n").filter { it != '\n' }.map { moveToDir[it]!! }

        for (dir in moves) {
            val impactedBoxes = getImpactedBoxes(boxes, robot, dir)
            val blocked = impactedBoxes.any { (b1, b2) -> walls.contains(b1.move(dir)) || walls.contains(b2.move(dir)) } || walls.contains(robot.move(dir))
            if (blocked) continue

            boxes.removeAll(impactedBoxes)
            boxes.addAll(impactedBoxes.map { (b1, b2) -> Pair(b1.move(dir), b2.move(dir)) })
            robot = robot.move(dir)
        }

        return boxes.map { it.first }.sumOf { (x, y) -> x + 100 * y }
    }

    val part1Example1 = part1(exampleInput1)
    val part1Example2 = part1(exampleInput2)
    val part2Example = part2(exampleInput2)

    check(part1Example1 == 2028) { -> "Part 1 example failed: Expected 2028, received $part1Example1" };
    check(part1Example2 == 10092) { -> "Part 1 example failed: Expected 10092, received $part1Example2" };
    check(part2Example == 9021) { -> "Part 2 example failed: Expected 9021, received $part2Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}