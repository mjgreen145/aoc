import kotlin.time.measureTime

typealias Coord3D = Triple<Int, Int, Int>

fun Coord3D.x(): Int = this.first
fun Coord3D.y(): Int = this.second
fun Coord3D.z(): Int = this.third

typealias Brick = List<Coord3D>

fun getBricks(lines: List<String>): List<Brick> {
    return lines.map { line ->
        val (start, end) = line.split("~")
        val (x1, y1, z1) = start.split(",").map { it.toInt() }
        val (x2, y2, z2) = end.split(",").map { it.toInt() }

        (x1..x2).flatMap { x ->
            (y1..y2).flatMap { y ->
                (z1..z2).map { z -> Coord3D(x, y, z) }
            }
        }
    }.sortedBy { brick -> brick.minOf { it.z() } }
}

fun main() {
    val exampleLines = readLines("day22-example")
    val lines = readLines("day22")

    fun part1(lines: List<String>): Int {
        val bricks = getBricks(lines)

        return 0
    }

    fun part2(lines: List<String>): Int {
        return 0
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 0) { -> "Part 1 example failed: Expected 0, received $part1Example" };
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}