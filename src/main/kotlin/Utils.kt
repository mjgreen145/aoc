import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

enum class Dir {
    North, South, East, West
}

enum class Dir8 {
    N, NE, E, SE, S, SW, W, NW
}

fun Any?.println() = println(this)

fun readInput(year: String, name: String) = Path("src/main/resources/$year/$name.txt").readText();

fun readLines(year: String, name: String) = Path("src/main/resources/$year/$name.txt").readLines()

fun pivotToCols(lines: List<String>): List<String> {
    return lines.first().mapIndexed { i, _ -> lines.map { it[i] }.joinToString("") }
}

typealias Coord = Pair<Int, Int>
fun Coord.x(): Int = this.first
fun Coord.y(): Int = this.second
fun Coord.move(dir: Dir, dist: Int): Coord {
    return when (dir) {
        Dir.North -> Coord(this.x(), this.y() - dist)
        Dir.South -> Coord(this.x(), this.y() + dist)
        Dir.East -> Coord(this.x() + dist, this.y())
        Dir.West -> Coord(this.x() - dist, this.y())
    }
}
fun Coord.move(dir: Dir8, dist: Int): Coord {
    return when (dir) {
        Dir8.N -> this.move(Dir.North, dist)
        Dir8.S -> this.move(Dir.South, dist)
        Dir8.E -> this.move(Dir.East, dist)
        Dir8.W -> this.move(Dir.West, dist)
        Dir8.NE -> this.move(Dir.North, dist).move(Dir.East, dist)
        Dir8.NW -> this.move(Dir.North, dist).move(Dir.West, dist)
        Dir8.SE -> this.move(Dir.South, dist).move(Dir.East, dist)
        Dir8.SW -> this.move(Dir.South, dist).move(Dir.West, dist)
    }
}

typealias Grid = List<String>

fun Grid.get(x: Int, y: Int): Char = this[y][x]
fun Grid.get(coord: Coord): Char = this.get(coord.x(), coord.y())
fun Grid.getOrEmpty(x: Int, y: Int): String {
    if (y in indices && x in 0..<this[y].length) {
        return this[y][x].toString()
    }
    return ""
}
fun Grid.getOrEmpty(coord: Coord): String = this.getOrEmpty(coord.x(), coord.y())
fun Grid.allCoords(): List<Coord> = this.indices.flatMap { y -> this[y].indices.map { x -> Pair(x, y) } }


fun adjacentCoords(grid: Grid, currentCoord: Coord): List<Coord> {
    val (x, y) = currentCoord
    return listOfNotNull(
        if (y > 0) Coord(x, y - 1) else null,
        if (y < grid.size - 1) Coord(x, y + 1) else null,
        if (x > 0) Coord(x - 1, y) else null,
        if (x < grid[0].length - 1) Coord(x + 1, y) else null,
    )
}