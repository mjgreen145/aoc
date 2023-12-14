import kotlin.time.measureTime

fun rollLines(lines: List<String>, direction: Dir): List<String> {
    return when (direction) {
        Dir.West -> lines.map { l ->
            l.split("#").joinToString("#") { it.filter { c -> c == 'O' } + it.filter { c -> c == '.' } }
        }

        Dir.East -> lines.map { l ->
            l.split("#").joinToString("#") { it.filter { c -> c == '.' } + it.filter { c -> c == 'O' } }
        }

        Dir.North -> pivotToCols(rollLines(pivotToCols(lines), Dir.West))
        Dir.South -> pivotToCols(rollLines(pivotToCols(lines), Dir.East))
    }
}

fun List<String>.roll(dir: Dir) = rollLines(this, dir)

fun spin(lines: List<String>, times: Int): List<String> {
    val seenStates = mutableMapOf<String, Int>();
    seenStates[lines.joinToString("")] = 0;
    var currentLines = lines;
    for (i in 1..times) {
        val newLines = currentLines.roll(Dir.North).roll(Dir.West).roll(Dir.South).roll(Dir.East)

        val prevCycle = seenStates[newLines.joinToString("")]
        if (prevCycle != null) {
            val remainingTimes = (times - prevCycle).mod(i - prevCycle)
            return spin(newLines, remainingTimes)
        }

        currentLines = newLines
        seenStates[currentLines.joinToString("")] = i
    }
    return currentLines
}

fun calcLoad(lines: List<String>): Int {
    return lines.mapIndexed { i, line -> line.count { c -> c == 'O' } * (lines.size - i) }.sum()
}

fun main() {
    val exampleLines = readLines("day14-example")
    val lines = readLines("day14")

    fun part1(lines: List<String>): Int {
        return calcLoad(lines.roll(Dir.North))
    }

    fun part2(lines: List<String>): Int {
        return calcLoad(spin(lines, 1000000000))
    }

    val part1Example = part1(exampleLines)
    check(part1Example == 136) { -> "Part 1 example failed: Expected 136, received $part1Example" };

    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleLines)
    check(part2Example == 64) { -> "Part 2 example failed: Expected 64, received $part2Example" };

    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")
}