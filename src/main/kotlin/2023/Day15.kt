package `2023`

import println
import readInput
import kotlin.time.measureTime

fun hash(string: String): Int {
    return string.fold(0) { total, c -> ((total + c.code) * 17).mod(256) }
}

fun main() {
    val exampleInput = readInput("2023", "day15-example")
    val input = readInput("2023", "day15")

    fun part1(input: String): Int {
        return input.split(",").map(::hash).sum()
    }

    fun part2(input: String): Long {
        val regex = Regex("([a-zA-Z]+)([-=])(\\d?)")
        val boxes = Array(256) { linkedMapOf<String, Int>() }

        input.split(",").forEach {
            val (_, label, op) = regex.find(it)!!.groupValues
            val box = hash(label)
            when (op) {
                "-" -> boxes[box].remove(label)
                "=" -> boxes[box][label] = regex.find(it)!!.groupValues[3].toInt()
                else -> throw Exception("Unsupported op, '$op'")
            }
        }

        return boxes.withIndex().sumOf { (i, box) ->
            box.values.foldIndexed(0) { j, total, lens ->
                total + (lens * (j + 1) * (i + 1))
            }.toLong()
        }
    }

    val part1Example = part1(exampleInput)
    check(part1Example == 1320) { -> "Part 1 example failed: Expected 1320, received $part1Example" };

    val timePart1 = measureTime { part1(input).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleInput)
    check(part2Example == 145L) { -> "Part 2 example failed: Expected 145, received $part2Example" };

    val timePart2 = measureTime { part2(input).println() }
    println("Part 2 took $timePart2")
}