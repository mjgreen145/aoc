data class Number(val value: Int, val row: Int, val charRange: IntRange);

fun main() {
    val lines = readLines("day3")

    fun part1(lines: List<String>, numbers: List<Number>): Int {
        return numbers.filter { number ->
            val charRange = IntRange(
                if (number.charRange.first == 0) 0 else number.charRange.first - 1,
                if (number.charRange.endInclusive == lines[number.row].length - 1) number.charRange.endInclusive else number.charRange.endInclusive + 1,
            )

            val prevRowChars = if (number.row > 0) lines[number.row - 1].substring(charRange) else ""
            val thisRowChars = lines[number.row].substring(charRange)
            val nextRowChars = if (number.row < lines.size - 1) lines[number.row + 1].substring(charRange) else ""
            "${prevRowChars}${thisRowChars}${nextRowChars}".filterNot { char -> char.isDigit() || char == '.' }.length > 0
        }.sumOf { number -> number.value }
    }

    fun part2(lines: List<String>, numbers: List<Number>): Int {
        val gearRatios: List<Int> = lines.flatMapIndexed { lineIndex, line ->
            Regex("\\*").findAll(line).toList().map { match ->
                val adjacentNumbers = numbers.filter { num ->
                    arrayOf(lineIndex - 1, lineIndex, lineIndex + 1).contains(num.row) &&
                            IntRange(num.charRange.first - 1, num.charRange.last + 1).contains(match.range.first);
                }
                if (adjacentNumbers.size == 2) adjacentNumbers.first.value * adjacentNumbers.last.value else 0;
            }
        }

        return gearRatios.sum()
    }

    val numbers = lines.flatMapIndexed { lineIndex, line ->
        Regex("\\d+").findAll(line).toList().mapIndexed { index, match ->
            Number(match.value.toInt(), lineIndex, match.range)
        }
    }

    part1(lines, numbers).println()
    part2(lines, numbers).println()
}