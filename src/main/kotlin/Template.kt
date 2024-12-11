import kotlin.time.measureTime

fun main() {
    val exampleInput = readLines("2024", "day1-example")
    val input = readLines("2024", "day1")

    fun part1(lines: List<String>): Int {
        return 0
    }

    fun part2(lines: List<String>): Int {
        return 0
    }

    val part1Example = part1(exampleInput)
    val part2Example = part2(exampleInput)

    check(part1Example == 0) { -> "Part 1 example failed: Expected 0, received $part1Example" };
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}