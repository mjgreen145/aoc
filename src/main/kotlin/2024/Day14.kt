package `2024`

import Coord
import Vector2D
import add
import mul
import readLines
import println
import kotlin.time.measureTime

fun main() {
    val exampleInput = readLines("2024", "day14-example")
    val input = readLines("2024", "day14")

    fun modulo(num: Int, divisor: Int): Int {
        return ((num % divisor) + divisor) % divisor
    }

    fun toRobots(lines: List<String>): List<Pair<Coord, Vector2D>> {
        return lines.map { line ->
            val (start, vel) = line.split(" ").map { str ->
                val (x, y) = str.substringAfter("=").split(",").map { it.toInt() }
                Pair(x, y)
            }
            Pair(start, vel)
        }
    }

    fun part1(lines: List<String>, xSize: Int, ySize: Int): Int {
        val finalRobots = toRobots(lines).map { (start, vel) ->
            val final = start.add(vel.mul(100))
            Pair(modulo(final.first, xSize), modulo(final.second, ySize))
        }
        return finalRobots.filterNot { (x, y) -> x == xSize / 2 || y == ySize / 2 }
            .partition { (x, _) -> x < xSize / 2 }
            .toList()
            .flatMap { it.partition { (_, y) -> y < ySize / 2 }.toList() }
            .map { it.size }
            .reduce(Int::times)
    }

    val part1Example = part1(exampleInput, 11, 7)
    check(part1Example == 12) { -> "Part 1 example failed: Expected 12, received $part1Example" };
    val timePart1 = measureTime { part1(input, 101, 103).println() }
    println("Part 1 took $timePart1")

    // Part 2 here: https://codepen.io/mjgreen145/pen/NPKbYxX
}