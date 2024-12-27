package `2024`

import println
import readInput
import kotlin.time.measureTime

typealias Gates = Map<String, List<String>>

fun main() {
    val exampleInput1 = readInput("2024", "day24-example1")
    val exampleInput2 = readInput("2024", "day24-example2")
    val input = readInput("2024", "day24")

    fun parseInput(text: String): Pair<Map<String, Int>, Gates> {
        val knownWires = text.substringBefore("\n\n")
            .split("\n").associate { str ->
                val (wire, value) = str.split(": ")
                wire to value.toInt()
            }.toMutableMap()

        val gates = text.substringAfter("\n\n").split("\n").associate { gate ->
            val (w1, op, w2, w3) = Regex("(.*) (.*) (.*) -> (.*)").find(gate)!!.groupValues.drop(1)
            w3 to listOf(w1, w2, op)
        }
        return Pair(knownWires, gates)
    }

    fun gate(a: Int, b: Int, op: String): Int {
        return when (op) {
            "AND" -> a and b
            "OR" -> a or b
            "XOR" -> a xor b
            else -> throw Exception("Unsupported op, '$op'")
        }
    }

    fun nodesStartingWith(c: Char, nodes: Iterable<String>): List<String> {
        return nodes.filter { node -> node.startsWith(c) }.sorted()
    }

    fun topologicalSort(knownWires: Map<String, Int>, gates: Gates): List<String>? {
        val sortedDag = mutableListOf<String>()
        val permanentMarks = mutableSetOf<String>()
        val tempMarks = mutableSetOf<String>()
        val allNodes = gates.keys + knownWires.keys

        fun visit(node: String) {
            if (permanentMarks.contains(node)) return
            if (tempMarks.contains(node)) {
                throw Error("Circular dependency detected in DAG, path travelled to $node twice")
            }
            tempMarks.add(node)
            for (depNode in gates[node]?.subList(0, 2) ?: emptyList()) {
                visit(depNode)
            }
            tempMarks.remove(node)
            permanentMarks.add(node)
            sortedDag.add(node)
        }

        try {
            allNodes.forEach { visit(it) }
        } catch (e: Error) {
            return null
        }

        return sortedDag
    }

    fun solveCircuitDag(knownWires: Map<String, Int>, gates: Gates): Map<String, Int>? {
        val sortedDag = topologicalSort(knownWires, gates)?: return null
        val known = knownWires.toMutableMap()
        sortedDag.forEach { node ->
            if (!known.containsKey(node)) {
                val (w1, w2, op) = gates[node]!!
                known[node] = gate(known[w1]!!, known[w2]!!, op)
            }
        }
        return known
    }

    fun deps(node: String, gates: Gates): Set<String> {
        val deps = gates[node] ?: return emptySet()
        return deps.toSet() + deps.flatMap { deps(it, gates) }.toSet()
    }

    fun buildSubgraph(endpoints: List<String>, allGates: Gates): Gates {
        val subgraph = mutableMapOf<String, List<String>>()

        (endpoints + endpoints.flatMap { deps(it, allGates) }).forEach { node ->
            val gate = allGates[node]
            if (gate != null) subgraph[node] = gate
        }
        return subgraph
    }

    fun padded(char: Char, digit: Int): String {
        return "$char${digit.toString().padStart(2, '0')}"
    }

    fun <K, V> Map<K, V>.swap(a: K, b: K): Map<K, V> {
        val swapped = this.toMutableMap()
        val temp = this[a]!!
        swapped[a] = this[b]!!
        swapped[b] = temp
        return swapped
    }

    fun part1(text: String): Long {
        val (knownWires, gates) = parseInput(text)
        return solveCircuitDag(knownWires, gates)!!.entries
            .filter { (k, _) -> k.startsWith('z') }
            .sortedBy { it.key }
            .joinToString("") { it.value.toString() }
            .reversed()
            .toLong(2)
    }

    fun part2(text: String): Int {
        var (knownWires, gates) = parseInput(text)

        gates = gates.swap("z09", "rkf")
        gates = gates.swap("z20", "jgb")
        gates = gates.swap("z24", "vcg")
        gates = gates.swap("rvc", "rrs")

        val zs = nodesStartingWith('z', gates.keys)

        val nulledWires = knownWires.mapValues { 0 }

        for (i in zs.indices) {
            i.println()
            val subgraph = buildSubgraph(zs.take(i + 1), gates)
            val subgraph2 = buildSubgraph(zs.take(i + 2), gates)
            val correctlyAddsFirstDigit = listOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1)).all { (x, y) ->
                val wires = nulledWires.toMutableMap()
                wires[padded('x', i)] = x
                wires[padded('y', i)] = y
                val results = solveCircuitDag(wires, subgraph)!!
                results[padded('z', i)] == x xor y
            }
            val correctlyAddsSecondDigit = listOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1)).all { (x, y) ->
                val wires = nulledWires.toMutableMap()
                wires[padded('x', i)] = x
                wires[padded('y', i)] = y
                val results = solveCircuitDag(wires, subgraph2)!!
                results[padded('z', i + 1)] == x and y
            }
            i.println()
            correctlyAddsFirstDigit.println()
            correctlyAddsSecondDigit.println()

            if (!correctlyAddsFirstDigit) {
                break
            }
        }

        return 0
    }

    val part1Example1 = part1(exampleInput1)
    val part1Example2 = part1(exampleInput2)

    check(part1Example1 == 4L) { -> "Part 1 example failed: Expected 4, received $part1Example1" };
    check(part1Example2 == 2024L) { -> "Part 1 example failed: Expected 2024, received $part1Example2" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}