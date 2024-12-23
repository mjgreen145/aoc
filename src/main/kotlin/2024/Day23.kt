package `2024`

import readLines
import println
import kotlin.time.measureTime

fun main() {
    val exampleInput = readLines("2024", "day23-example")
    val input = readLines("2024", "day23")

    fun cyclesOfSize(map: Map<String, Set<String>>, size: Int): Set<Set<String>> {
        val toCheck = map.keys.map { k -> Pair(k, listOf(k)) }.toMutableList()
        val sets = mutableSetOf<Set<String>>()

        while (toCheck.isNotEmpty()) {
            val (key, seen) = toCheck.removeFirst()
            if (seen.size == size) {
                map[key]!!.forEach { next -> if (seen.first() == next) sets.add(seen.toSet()) }
                continue
            }

            map[key]!!.forEach { next -> if (!seen.contains(next)) toCheck.add(Pair(next, seen + next)) }
        }

        return sets
    }

    fun fullyConnectedSets(map: Map<String, Set<String>>): Set<Set<String>> {
        val toCheck = map.keys.map { k -> setOf(k) }.toMutableList()
        val seenSets = mutableSetOf<Set<String>>()

        while (toCheck.isNotEmpty()) {
            val set = toCheck.removeFirst()
            seenSets.add(set)

            set.flatMap { map[it]!! }.filter { !set.contains(it) }.forEach { newNode ->
                if (set.all { map[newNode]!!.contains(it) } && !seenSets.contains(set + newNode)) {
                    toCheck.addFirst(set + newNode)
                }
            }
        }

        return seenSets
    }

    fun parse(lines: List<String>): Map<String, Set<String>> {
        val map = mutableMapOf<String, MutableSet<String>>()
        lines.forEach { str ->
            val (a, b) = str.split('-')
            map.getOrPut(a) { mutableSetOf() }.add(b)
            map.getOrPut(b) { mutableSetOf() }.add(a)
        }
        return map
    }

    fun part1(lines: List<String>): Int {
        return cyclesOfSize(parse(lines), 3).filter { set -> set.any { it.startsWith('t') } }.size
    }

    fun part2(lines: List<String>): String {
        return fullyConnectedSets(parse(lines)).maxBy { it.size }.sorted().joinToString(",")
    }

    val part1Example = part1(exampleInput)
    val part2Example = part2(exampleInput)

    check(part1Example == 7) { -> "Part 1 example failed: Expected 7, received $part1Example" };
    check(part2Example == "co,de,ka,ta") { -> "Part 2 example failed: Expected co,de,ka,ta, received $part2Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}