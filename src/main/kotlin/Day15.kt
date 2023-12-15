import kotlin.time.measureTime

fun hash(string: String): Int {
    return string.fold(0) { total, c -> ((total + c.code) * 17).mod(256) }
}

fun main() {
    val exampleInput = readInput("day15-example")
    val input = readInput("day15")

    fun part1(input: String): Int {
        return input.split(",").map(::hash).sum()
    }

    fun part2(input: String): Int {
        val regex = Regex("([a-zA-Z]+)([-=])(\\d?)")
        val boxes = (0..255).associateWith { linkedMapOf<String, Int>() }

        input.split(",").forEach {
            val (_, label, op) = regex.find(it)!!.groupValues
            val box = hash(label)
            when (op) {
                "-" -> boxes[box]!!.remove(label)
                "=" -> boxes[box]!![label] = regex.find(it)!!.groupValues[3].toInt()
                else -> throw Exception("Unsupported op, '$op'")
            }
        }

        return ((0..255).sumOf { i ->
            val values = boxes[i]!!.values
            val result: Int = if (values.isEmpty()) 0 else
                values.foldIndexed(0) { j, total, lens -> total + (lens * (j + 1) * (i + 1)) }
            result
        })
    }

    val part1Example = part1(exampleInput)
    check(part1Example == 1320) { -> "Part 1 example failed: Expected 1320, received $part1Example" };

    val timePart1 = measureTime { part1(input).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleInput)
    check(part2Example == 145) { -> "Part 2 example failed: Expected 145, received $part2Example" };

    val timePart2 = measureTime { part2(input).println() }
    println("Part 2 took $timePart2")
}