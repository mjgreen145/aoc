package `2024`

import println
import readLines
import kotlin.math.floor
import kotlin.math.round
import kotlin.time.measureTime

typealias Rule = Pair<Int, Int>
typealias Update = List<Int>

fun main() {
    val exampleLines = readLines("2024", "day5-example")
    val lines = readLines("2024", "day5")

    fun parseInput(lines: List<String>): Pair<List<Rule>, List<Update>> {
        val (rulesText, updateText) = lines.filter { it.isNotEmpty() }.partition { it.contains('|') }
        val rules = rulesText.map { text ->
            val (a, b) = text.split('|');
            Pair(a.toInt(), b.toInt())
        }
        val updates = updateText.map { text -> text.split(',').map { it.toInt() } }
        return Pair(rules, updates)
    }

    fun isCorrect(update: Update, rules: List<Rule>): Boolean {
        return rules.filter { update.containsAll(it.toList()) }.all { (a, b) -> update.indexOf(a) < update.indexOf(b) }
    }

    fun <T> middleItem(xs: List<T>): T {
        return xs[xs.size / 2]
    }

    fun part1(lines: List<String>): Int {
        val (rules, updates) = parseInput(lines)
        return updates.filter { isCorrect(it, rules) }.sumOf { middleItem(it) }
    }

    fun part2(lines: List<String>): Int {
        val (rules, updates) = parseInput(lines)
        val ruleSet = rules.toSet()
        val ruleComparator = Comparator<Int> { a, b ->
            when {
                ruleSet.contains(Pair(a, b)) -> 1
                ruleSet.contains(Pair(b, a)) -> -1
                else -> 0
            }
        }
        return updates.filterNot { isCorrect(it, rules) }.map { it.sortedWith(ruleComparator) }.sumOf { middleItem(it) }
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 143) { -> "Part 1 example failed: Expected 143, received $part1Example" };
    check(part2Example == 123) { -> "Part 2 example failed: Expected 123, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}