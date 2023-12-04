import kotlin.math.pow
import kotlin.time.measureTime

fun twoToPowerOf(power: Int): Int {
    val two = 2.0;
    return two.pow(power).toInt();
}

fun numWinningNumbers(line: String): Int {
    val lists = line.substringAfter(":").split("|")
    val winningNums = lists.first.trim().split(Regex("\\s+")).map { it.toInt() }.toSet()
    val lineNums = lists.last.trim().split(Regex("\\s+")).map { it.toInt() }.toSet()

    return lineNums.intersect(winningNums).size;
}

fun main() {
    val lines = readInput("day4")

    fun part1(lines: List<String>): Int {
        return lines.sumOf { line ->
            val numMatches = numWinningNumbers(line)
            if (numMatches > 0) twoToPowerOf(numMatches - 1) else 0
        }
    }

    fun part2(lines: List<String>): Int {
        val counts = IntArray(lines.size) { 1 }

        lines.forEachIndexed { index, line ->
            val numMatches = numWinningNumbers(line)
            if (numMatches > 0) {
                IntRange(
                    (index + 1).coerceAtMost(lines.size - 1),
                    (index + numMatches).coerceAtMost(lines.size - 1)
                ).forEach { i -> counts[i] = counts[i] + counts[index] }
            }
        }

        return counts.sum();
    }

    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")
    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")

}