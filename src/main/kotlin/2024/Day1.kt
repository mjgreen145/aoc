package `2024`

import println
import readLines
import kotlin.math.abs
import kotlin.time.measureTime

fun main() {
    val exampleLines = readLines("2024", "day1-example")
    val lines = readLines("2024", "day1")

    fun part1(lines: List<String>): Int {
        val firstList = mutableListOf<Int>();
        val secondList = mutableListOf<Int>();
        lines.map { line -> line.split("   ") }.forEach { (f, s) ->
            firstList.add(f.toInt());
            secondList.add(s.toInt());
        }
        firstList.sort()
        secondList.sort()

        val dists = firstList.mapIndexed { index, fId ->
            abs(fId - secondList[index])
        }
        return dists.sum();
    }

    fun part2(lines: List<String>): Int {
        val firstList = mutableListOf<Int>();
        val counts = hashMapOf<Int, Int>();
        lines.map { line -> line.split("   ") }.forEach { (f, s) ->
            firstList.add(f.toInt());
            counts[s.toInt()] = counts.getOrDefault(s.toInt(), 0) + 1
        }

        return firstList.sumOf { i ->
            i * counts.getOrDefault(i, 0);
        }
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 11) { -> "Part 1 example failed: Expected 11, received $part1Example" };
    check(part2Example == 31) { -> "Part 2 example failed: Expected 31, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}