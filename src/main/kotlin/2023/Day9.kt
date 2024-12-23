package `2023`

import println
import readLines
import kotlin.time.measureTime

fun diffs(numSeq: List<Int>): List<Int> {
    return numSeq.windowed(2).map { (x, y) -> y - x }
}

fun nextNum(numSeq: List<Int>): Int {
    return numSeq.last() + (if (numSeq.toSet().size != 1) nextNum(diffs(numSeq)) else 0)
}

fun prevNum(numSeq: List<Int>): Int {
    return numSeq.first() - (if (numSeq.toSet().size != 1) prevNum(diffs(numSeq)) else 0)
}

fun main() {
    val exampleSeqs = readLines("2023", "day9-example").map { line -> line.split(" ").map { it.toInt() } }
    val seqs = readLines("2023", "day9").map { line -> line.split(" ").map { it.toInt() } }

    fun part1(numSeqs: List<List<Int>>): Int {
        return numSeqs.sumOf(::nextNum)
    }

    fun part2(numSeqs: List<List<Int>>): Int {
        return numSeqs.sumOf(::prevNum)
    }

    val part1Example = part1(exampleSeqs)
    check(part1Example == 114) { -> "Part 1 example failed: Expected 114, received $part1Example" };

    val timePart1 = measureTime { part1(seqs).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleSeqs)
    check(part2Example == 2) { -> "Part 2 example failed: Expected 2, received $part2Example" };

    val timePart2 = measureTime { part2(seqs).println() }
    println("Part 2 took $timePart2")
}