package `2024`

import println
import readInput
import kotlin.time.measureTime

fun main() {
    val exampleLines = readInput("2024", "day9-example")
    val lines = readInput("2024", "day9")

    fun checksum(blocks: List<Int?>): Long {
        return blocks.mapIndexed { idx, id -> if (id == null) 0 else idx.toLong() * id.toLong() }.sum()
    }

    fun part1(text: String): Long {
        val blocks = text.chunked(2).foldIndexed(mutableListOf<Int?>()) { id, blocks, str ->
            blocks.addAll(List(str[0].digitToInt()) { _ -> id })
            blocks.addAll(List(str.getOrElse(1) { _ -> '0' }.digitToInt()) { _ -> null })
            blocks
        }

        while (blocks.contains(null)) {
            val last = blocks.removeLast()
            if (last != null) {
                val nullIndex = blocks.indexOf(null)
                if (nullIndex >= 0) {
                    blocks[nullIndex] = last
                }
            }
        }

        return checksum(blocks)
    }

    fun part2(text: String): Long {
        val sizedBlocks = text.chunked(2).foldIndexed(mutableListOf<Pair<Int?, Int>>()) { id, blocks, str ->
            blocks.add(Pair(id, str[0].digitToInt()))
            blocks.add(Pair(null, str.getOrElse(1) { _ -> '0' }.digitToInt()))
            blocks
        }

        val maxId = sizedBlocks.findLast { (v) -> v != null }!!.first!!

        for (id in maxId downTo 0) {
            val blockIdx = sizedBlocks.indexOfFirst { (v, _) -> v == id }
            val (value, size) = sizedBlocks[blockIdx];

            val gapIndex = sizedBlocks.indexOfFirst { (v, s) -> v == null && size <= s }
            if (gapIndex in 0..<blockIdx) {
                val (_, space) = sizedBlocks[gapIndex]
                sizedBlocks[blockIdx] = Pair(null, size)
                sizedBlocks[gapIndex] = Pair(null, space - size)
                sizedBlocks.add(gapIndex, Pair(value, size))
            }
        }

        val blocks = sizedBlocks.flatMap { (value, size)  -> List(size) {_ -> value} }
        return checksum(blocks)
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 1928L) { -> "Part 1 example failed: Expected 1928, received $part1Example" };
    check(part2Example == 2858L) { -> "Part 2 example failed: Expected 2858, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}