import kotlin.time.measureTime

fun main() {
    val lines = readLines("day5")

    fun part1(lines: List<String>): Int {
        return 0
    }

    fun part2(lines: List<String>): Int {
        return 0
    }

    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")
    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")
}