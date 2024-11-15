package `2023`

import println
import readLines
import kotlin.time.measureTime

data class Node(val left: String, val right: String)

fun buildGraph(lines: List<String>): Map<String, Node> {
    val map = mutableMapOf<String, Node>();
    val regex = Regex("([A-Z0-9]{3}) = \\(([A-Z0-9]{3}), ([A-Z0-9]{3})\\)")
    lines.forEach { line ->
        val (_, key, left, right) = regex.find(line)!!.groupValues
        map[key] = Node(left, right)
    }
    return map;
}

fun stepsToFinish(directions: String, map: Map<String, Node>, start: String, endPred: (String) -> Boolean): Int {
    var currentNode = start
    var steps = 0
    while (!endPred(currentNode)) {
        val dir = directions[steps.mod(directions.length)]
        currentNode = if (dir == 'L') map[currentNode]!!.left else map[currentNode]!!.right
        steps++
    }
    return steps
}

fun lcm(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun lcmMultiple(ints: List<Long>): Long {
    var result = ints[0]
    for (i in 1..<ints.size) {
        result = lcm(result, ints[i])
    }
    return result
}

fun main() {
    fun part1(lines: List<String>): Int {
        val directions = lines.first();
        val map = buildGraph(lines.drop(2));
        return stepsToFinish(directions, map, "AAA") { it == "ZZZ" }
    }

    fun part2(lines: List<String>): Long {
        val directions = lines.first();
        val map = buildGraph(lines.drop(2));

        var currentNodes = map.keys.filter { s -> s.endsWith('A') }.toList()
        val stepTotals = currentNodes.map { node -> stepsToFinish(directions, map, node) { it.endsWith('Z') }.toLong() }

        stepTotals.println()
        return lcmMultiple(stepTotals)
    }

    val exampleLines = readLines("2023","day8-example")
    val exampleLinesPart2 = readLines("2023","day8-example2")
    val lines = readLines("2023","day8")

    val part1Example = part1(exampleLines)
    check(part1Example == 2) { -> "Part 1 example failed: Expected 2, received $part1Example" };

    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleLinesPart2)
    check(part2Example == 6L) { -> "Part 2 example failed: Expected 6, received $part2Example" };

    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")
}