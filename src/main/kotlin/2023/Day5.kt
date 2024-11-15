package `2023`

import println
import readInput
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTime

fun rangeIntersection(a: LongRange, b: LongRange): LongRange? {
    if (a.last < b.first || b.last < a.first) {
        return null;
    }
    return LongRange(a.first.coerceAtLeast(b.first), a.last.coerceAtMost(b.last))
}

fun splitBy(a: LongRange, b: LongRange): Pair<LongRange?, List<LongRange>> {
    val rangeBefore = if (a.first < b.first) LongRange(a.first, min(a.last, b.first - 1)) else null
    val rangeOverlap = rangeIntersection(a, b);
    val rangeAfter = if (a.last > b.last) LongRange(max(b.last + 1, a.first), a.last) else null

    return Pair(rangeOverlap, listOfNotNull(rangeBefore, rangeAfter));
}

typealias LocationMap = List<Pair<LongRange, Long>>

fun getSeeds(input: String): List<Long> {
    return input.split("\n\n")
        .first()
        .substringAfter(": ")
        .split(" ")
        .map { it.toLong() }
}

fun getSeedRanges(input: String): List<LongRange> {
    return input.split("\n\n")
        .first()
        .substringAfter(": ")
        .split(" ")
        .map { it.toLong() }
        .chunked(2)
        .map { (rangeStart, rangeLength) -> LongRange(rangeStart, rangeStart + rangeLength - 1) }
}

fun getLocationMaps(input: String): List<LocationMap> {
    return input.split("\n\n")
        .drop(1)
        .map {
            it.split("\n").drop(1).map { line ->
                val (destRangeStart, sourceRangeStart, rangeLength) = line.split(" ").map { num -> num.toLong() }
                Pair(LongRange(sourceRangeStart, sourceRangeStart + rangeLength - 1), destRangeStart - sourceRangeStart)
            }
        }
}

fun mapOneLocation(locationMap: LocationMap, ranges: List<LongRange>): List<LongRange> {
    val processedRanges = mutableListOf<LongRange>()
    var rangesToProcess = ranges.toMutableList()

    for ((mapRange, mapDiff) in locationMap) {
        rangesToProcess = rangesToProcess.flatMap { range ->
            val (overlap, others) = splitBy(range, mapRange)
            if (overlap != null) {
                processedRanges.add(LongRange(overlap.first + mapDiff, overlap.last + mapDiff))
            }
            others
        }.toMutableList()
    }
    processedRanges.addAll(rangesToProcess)

    return processedRanges
}

fun main() {
    val exampleText = readInput("2023", "day5-example")
    val text = readInput("2023", "day5")

    fun part1(input: String): Long {
        val seeds = getSeeds(input);
        val maps = getLocationMaps(input)

        val locations = seeds.map { seed ->
            maps.fold(seed) { value, map ->
                val match = map.find { (range, _) -> range.contains(value) }
                if (match != null) value + match.second else value
            }
        }

        return locations.min()
    }

    fun part2(input: String): Long {
        val seedRanges = getSeedRanges(input)
        val locationMaps = getLocationMaps(input)

        val finalRanges = locationMaps.fold(seedRanges) { ranges, locationMap ->
            mapOneLocation(locationMap, ranges)
        }
        return finalRanges.minOf { range -> range.first }
    }

    val part1Example = part1(exampleText)
    val part2Example = part2(exampleText)

    check(part1Example == 35L) { -> "Part 1 example failed: Expected 35, received $part1Example" };
    check(part2Example == 46L) { -> "Part 2 example failed: Expected 46, received $part2Example" };

    val timePart1 = measureTime { part1(text).println() }
    val timePart2 = measureTime { part2(text).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}