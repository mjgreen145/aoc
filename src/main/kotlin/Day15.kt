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
        return 0
    }

    val part1Example = part1(exampleInput)
    check(part1Example == 1320) { -> "Part 1 example failed: Expected 1320, received $part1Example" };

    val timePart1 = measureTime { part1(input).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleInput)
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    val timePart2 = measureTime { part2(input).println() }
    println("Part 2 took $timePart2")
}