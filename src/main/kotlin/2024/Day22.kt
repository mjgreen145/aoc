package `2024`

import readLines
import println
import kotlin.time.measureTime

fun main() {
    val exampleInput = readLines("2024", "day22-example")
    val exampleInput2 = readLines("2024", "day22-example2")
    val input = readLines("2024", "day22")

    fun Long.mix(num: Long): Long = this xor num
    fun Long.prune(): Long = this % 16777216

    fun nextNum(num: Long): Long {
        val a = num.mix(num * 64).prune()
        val b = a.mix(a / 32).prune()
        return b.mix(b * 2048).prune()
    }

    fun part1(lines: List<String>): Long {
        return lines.sumOf { (1..2000).fold(it.toLong()) { num, _ -> nextNum(num) } }
    }

    fun part2(lines: List<String>): Long {
        val bananas = mutableMapOf<String, MutableMap<String, Long>>()
        lines.forEach { line ->
            (0..2000).fold(Pair(line.toLong(), listOf<Long>())) { (num, changes), _ ->
                val next = nextNum(num)
                val price = num % 10
                val change = (next % 10) - price
                if (changes.size == 4) {
                    val lineMap = bananas.getOrPut(changes.joinToString(",")) { mutableMapOf() }
                    if (!lineMap.containsKey(line)) lineMap[line] = price
                }
                Pair(next, changes.takeLast(3) + change)
            }
        }
        return bananas.mapValues { (_, map) -> map.values.sum() }.maxOf { (_, v) -> v }
    }

    val part1Example = part1(exampleInput)
    val part2Example = part2(exampleInput2)

    check(part1Example == 37327623L) { -> "Part 1 example failed: Expected 37327623, received $part1Example" };
    check(part2Example == 23L) { -> "Part 2 example failed: Expected 23, received $part2Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}