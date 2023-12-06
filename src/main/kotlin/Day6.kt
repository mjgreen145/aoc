import kotlin.math.floor
import kotlin.time.measureTime

fun iter(a: Long, b: Long, totalTime: Long, timeToBeat: Long): Long {
    println("$a, $b");
    if (a == b) {
        return if (a * (totalTime - a) <= timeToBeat) a + 1 else a
    }

    val mid = floor((b - ((b - a) / 2)).toDouble())
    return if (mid * (totalTime - mid) <= timeToBeat) {
        iter((mid + 1).toLong(), b, totalTime, timeToBeat)
    } else {
        iter(a, mid.toLong() - 1, totalTime, timeToBeat);
    }
}


fun main() {
    fun part1(): Int {
        return 2269432
    }

    fun part2(): Long {
        val timeAvail = 49787980
        val timeToBeat = 298118510661181

        val pivot = iter(0, (timeAvail / 2).toLong(), timeAvail.toLong(), timeToBeat);

        return (timeAvail - 2 * pivot) + 1;
    }

    val timePart1 = measureTime { part1().println() }
    println("Part 1 took $timePart1")
    val timePart2 = measureTime { part2().println() }
    println("Part 2 took $timePart2")
}