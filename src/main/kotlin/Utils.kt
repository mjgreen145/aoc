import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

enum class Dir {
    North, South, East, West
}

fun Any?.println() = println(this)

fun readInput(name: String) = Path("src/main/resources/$name.txt").readText();

fun readLines(name: String) = Path("src/main/resources/$name.txt").readLines()

fun pivotToCols(lines: List<String>): List<String> {
    return lines.first().mapIndexed { i, _ -> lines.map { it[i] }.joinToString("") }
}

typealias Coord = Pair<Int, Int>