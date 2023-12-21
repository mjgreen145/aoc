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
fun Coord.x(): Int = this.first
fun Coord.y(): Int = this.second

typealias Grid = List<String>

fun Grid.get(x: Int, y: Int): Char = this[y][x]
fun Grid.get(coord: Coord): Char = this.get(coord.x(), coord.y())

fun adjacentCoords(grid: Grid, currentCoord: Coord): List<Coord> {
    val (x, y) = currentCoord
    return listOfNotNull(
        if (y > 0) Pair(x, y - 1) else null,
        if (y < grid.size - 1) Pair(x, y + 1) else null,
        if (x > 0) Pair(x - 1, y) else null,
        if (x < grid[0].length - 1) Pair(x + 1, y) else null,
    )
}