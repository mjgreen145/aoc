import kotlin.time.measureTime

fun roll(lines: List<String>, direction: Dir): List<String> {
    return when (direction) {
        Dir.West -> lines.map { l ->
            l.split("#").joinToString("#") { it.filter { c -> c == 'O' } + it.filter { c -> c == '.' } }
        }

        Dir.East -> lines.map { l ->
            l.split("#").joinToString("#") { it.filter { c -> c == '.' } + it.filter { c -> c == 'O' } }
        }

        Dir.North -> pivotToCols(roll(pivotToCols(lines), Dir.West))
        Dir.South -> pivotToCols(roll(pivotToCols(lines), Dir.East))
    }
}

fun calcLoad(lines: List<String>): Int {
    return lines.mapIndexed { i, line -> line.count { c -> c == 'O' } * (lines.size - i) }.sum()
}

fun main() {
    val exampleLines = readLines("day14-example")
    val lines = readLines("day14")

    fun part1(lines: List<String>): Int {
        return calcLoad(roll(lines, Dir.North))
    }

    fun part2(lines: List<String>): Int {
        return 0
    }

    val part1Example = part1(exampleLines)
    check(part1Example == 136) { -> "Part 1 example failed: Expected 136, received $part1Example" };

    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleLines)
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")
}