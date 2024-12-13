import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

fun Any?.println() = println(this)

fun readInput(year: String, name: String) = Path("src/main/resources/$year/$name.txt").readText();

fun readLines(year: String, name: String) = Path("src/main/resources/$year/$name.txt").readLines()

fun pivotToCols(lines: List<String>): List<String> {
    return lines.first().mapIndexed { i, _ -> lines.map { it[i] }.joinToString("") }
}

// Grid stuff
enum class Turn {
    Right, Left
}
enum class Dir {
    North, South, East, West
}
enum class Dir8 {
    N, NE, E, SE, S, SW, W, NW
}

fun turn(dir: Dir, turn: Turn): Dir {
    return when (dir) {
        Dir.North -> if (turn == Turn.Left) Dir.West else Dir.East
        Dir.South -> if (turn == Turn.Left) Dir.East else Dir.West
        Dir.East -> if (turn == Turn.Left) Dir.North else Dir.South
        Dir.West -> if (turn == Turn.Left) Dir.South else Dir.North
    }
}

typealias Coord = Pair<Int, Int>
typealias Vector2D = Pair<Int, Int>

fun Coord.x(): Int = this.first
fun Coord.y(): Int = this.second
fun Coord.move(dir: Dir, dist: Int = 1): Coord {
    return when (dir) {
        Dir.North -> Coord(this.x(), this.y() - dist)
        Dir.South -> Coord(this.x(), this.y() + dist)
        Dir.East -> Coord(this.x() + dist, this.y())
        Dir.West -> Coord(this.x() - dist, this.y())
    }
}

fun Coord.move(dir: Dir8, dist: Int = 1): Coord {
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
fun Coord.add(other: Coord): Vector2D {
    return Pair(this.x() + other.x(), this.y() + other.y())
}
fun Coord.minus(other: Coord): Vector2D {
    return Pair(this.x() - other.x(), this.y() - other.y())
}
fun Coord.adjacent(): Set<Coord> {
    return setOf(
        Pair(this.x() - 1, this.y()),
        Pair(this.x() + 1, this.y()),
        Pair(this.x(), this.y() - 1),
        Pair(this.x(), this.y() + 1),
    )
}

fun Vector2D.mul(scalar: Int): Vector2D {
    return Pair(this.x() * scalar, this.y() * scalar)
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
fun Grid.containsCoord(c: Coord): Boolean {
    return (c.x() in this.first().indices && c.y() in this.indices)
}

fun Grid.adjacentCoords(c: Coord): List<Coord> {
    return Dir.entries.map { dir -> c.move(dir, 1) }.filter { this.containsCoord(it) }
}

fun Grid.findChar(c: Char): Coord {
    this.indices.forEach { y ->
        val x = this[y].indexOf(c)
        if (x != -1) {
            return Pair(x, y)
        }
    }
    throw Exception("Char $c not found")
}

data class LineFormula(val a: Double, val b: Double, val c: Double)