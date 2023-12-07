import kotlin.time.measureTime

fun main() {
    val exampleLines = readLines("day1-example")
    val lines = readLines("day1")

    fun part1(lines: List<String>): Int {
        return 0
    }

    fun part2(lines: List<String>): Int {
        return 0
    }

    check(part1(exampleLines) == 0) { -> "Part 1 example passed" };
    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")

    check(part2(exampleLines) == 0) { -> "Part 2 example passed" };
    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")
}