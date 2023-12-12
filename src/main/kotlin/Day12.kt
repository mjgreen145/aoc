import kotlin.math.pow
import kotlin.time.measureTime

fun combinations(springs: String, groups: List<Int>, regex: Regex): List<String> {
    fun iter(currentStr: String, remainingStr: String, remainingGroups: List<Int>): List<String> {
        if (remainingStr == "") {
            return listOf(currentStr)
        }
        if (remainingGroups.isEmpty()) {
            return listOf(currentStr + remainingStr.map { '.' })
        }

        val nextGroup = remainingGroups.first()
        if (remainingStr.first() == '.') {
            return iter("$currentStr.", remainingStr.drop(1), remainingGroups)
        }
        if (remainingStr.first() == '#') {
            return iter(currentStr + "#".repeat(nextGroup), remainingStr.drop(nextGroup), remainingGroups.drop(1))
        }
        return listOfNotNull(
            if (("$currentStr.${remainingStr.drop(1)}").matches(regex)) Triple(
                "$currentStr.",
                remainingStr.drop(1),
                remainingGroups
            ) else null,
            if (("$currentStr#${remainingStr.drop(1)}").matches(regex)) Triple(
                currentStr + "#".repeat(nextGroup),
                remainingStr.drop(nextGroup),
                remainingGroups.drop(1)
            ) else null
        ).flatMap { (current, remain, remainGroups) -> iter(current, remain, remainGroups) }
    }

    return iter("", springs, groups)
}

fun repeatingCombinations(springs: String, groups: List<Int>, numTimes: Int): List<String> {
    val innerRegex = groups.map { num -> "[#?]{$num}[.?X]+" }.joinToString("")
    val regex = Regex("^[.?]*(${innerRegex}){$numTimes}$")
    return combinations(
        Array(numTimes) { springs }.joinToString("?") + 'X',
        Array(numTimes) { 0 }.flatMap { groups },
        regex
    )
}

fun main() {
    val exampleLines = readLines("day12-example")
    val lines = readLines("day12")

    fun part1(lines: List<String>): Int {
        return lines.map { line ->
            val (springs, groupsStr) = line.split(" ")
            val groups = groupsStr.split(",").map { it.toInt() }
            val regex = Regex("^[.?]*${groups.map { num -> "[#?]{$num}" }.joinToString("[.?]+")}[.?]*$")
            combinations(springs, groups, regex)
        }.sumOf { it.size }
    }

    fun part2(lines: List<String>): Long {
        return lines.map { line ->
            val (springs, groupsStr) = line.split(" ")
            val groups = groupsStr.split(",").map { it.toInt() }

            val oneTimeCombinations = repeatingCombinations(springs, groups, 1);
            val twoTimeCombinations = repeatingCombinations(springs, groups, 2);
            val threeTimeCombinations = repeatingCombinations(springs, groups, 3);
            val fourTimeCombinations = repeatingCombinations(springs, groups, 4);

            val divisor = twoTimeCombinations.size / oneTimeCombinations.size

            println("Uh oh, divisors: $divisor, ${threeTimeCombinations.size / twoTimeCombinations.size}, ${fourTimeCombinations.size / threeTimeCombinations.size} . String: $springs ${oneTimeCombinations.size}, ${twoTimeCombinations.size}, ${threeTimeCombinations.size}, ${fourTimeCombinations.size}")
            oneTimeCombinations.size.toLong() * (divisor.toDouble().pow(4).toLong())
        }.sum()
    }

    val part1Example = part1(exampleLines)
    check(part1Example == 21) { -> "Part 1 example failed: Expected 21, received $part1Example" };

    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleLines)
    check(part2Example == 525152L) { -> "Part 2 example failed: Expected 525152, received $part2Example" };

//    val timePart2 = measureTime { part2(lines).println() }
//    println("Part 2 took $timePart2")
}