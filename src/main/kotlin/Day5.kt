import kotlin.time.measureTime

fun rangeIntersection(a: LongRange, b: LongRange): LongRange? {
    if (a.last < b.first || b.last < a.first) {
        return null;
    }
    return LongRange(a.first.coerceAtLeast(b.first), a.last.coerceAtMost(b.last))
}

fun splitBy(a: LongRange, b: LongRange): Pair<LongRange?, List<LongRange>> {
    val rangeBefore = if (a.first < b.first) LongRange(a.first, b.first - 1) else null
    val rangeOverlap = rangeIntersection(a, b);
    val rangeAfter = if (a.last > b.last) LongRange(b.last + 1, a.last) else null

    return Pair(rangeOverlap, listOfNotNull(rangeBefore, rangeAfter));
}

fun main() {
    val text = readInput("day5")
    val blocks = text.split("\n\n")

    val maps = blocks.drop(1).map {
        it.split("\n").drop(1).map { line ->
            val (destRangeStart, sourceRangeStart, rangeLength) = line.split(" ").map { num -> num.toLong() }
            Pair(LongRange(sourceRangeStart, sourceRangeStart + rangeLength - 1), destRangeStart - sourceRangeStart)
        }
    }

    fun part1(input: String): Long {
        val seeds = blocks.first().substringAfter(": ").split(" ").map { it.toLong() }

        val locations = seeds.map { seed ->
            maps.fold(seed) { value, map ->
                val match = map.find { (range, _) -> range.contains(value) }
                if (match != null) value + match.second else value
            }
        }

        return locations.min()
    }

    fun part2(lines: String): Long {
        val seedRanges = blocks.first().substringAfter(": ").split(" ").map { it.toLong() }.chunked(2)
            .map { (rangeStart, rangeLength) -> LongRange(rangeStart, rangeStart + rangeLength - 1) }

        val finalRanges = maps.foldIndexed(seedRanges) { i, ranges, mapBlock ->
            println("Doing block $i of ${maps.size}")
            println(ranges.size)
            mapBlock.flatMap { map ->
                ranges.flatMap { range ->
                    val (overlap, others) = splitBy(range, map.first)
                    val mappedRange = if (overlap != null) LongRange(
                        overlap.first + map.second,
                        overlap.last + map.second
                    ) else null
                    val combined: List<LongRange> = if (mappedRange != null) {
                        val newRanges = others.toMutableList()
                        newRanges.addLast(mappedRange);
                        newRanges
                    } else others
                    combined
                }
            }
        }

        return finalRanges.sortedBy { it.first }.first().first
    }

    val timePart1 = measureTime { part1(text).println() }
    println("Part 1 took $timePart1")
//    val timePart2 = measureTime { part2(text).println() }
//    println("Part 2 took $timePart2")
}