import kotlin.time.measureTime

typealias Grid = List<String>

fun Grid.get(x: Int, y: Int): Char = this[y][x]

fun nextCoord(coord: Coord, dir: Dir, grid: Grid): Coord? {
    val (x, y) = coord
    return when (dir) {
        Dir.North -> if (y > 0) Pair(x, y - 1) else null
        Dir.South -> if (y < grid.size - 1) Pair(x, y + 1) else null
        Dir.West -> if (x > 0) Pair(x - 1, y) else null
        Dir.East -> if (x < grid[0].length - 1) Pair(x + 1, y) else null
    }
}

fun turnDir(dir: Dir, mirror: Char): Dir {
    return when (dir) {
        Dir.North -> if (mirror == '/') Dir.East else Dir.West
        Dir.South -> if (mirror == '/') Dir.West else Dir.East
        Dir.East -> if (mirror == '/') Dir.North else Dir.South
        Dir.West -> if (mirror == '/') Dir.South else Dir.North
    }
}

fun splitDirs(dir: Dir, split: Char): List<Dir> {
    return when (dir) {
        Dir.North, Dir.South -> if (split == '-') listOf(Dir.East, Dir.West) else listOf(dir)
        Dir.East, Dir.West -> if (split == '-') listOf(dir) else listOf(Dir.North, Dir.South)
    }
}

fun getNextTiles(tile: Pair<Coord, Dir>, grid: Grid): List<Pair<Coord, Dir>> {
    val (coord, dir) = tile
    val (x, y) = coord
    val char = grid.get(x, y)

    return when (char) {
        '.' -> {
            val next = nextCoord(coord, dir, grid)
            if (next != null) listOf(Pair(next, dir)) else listOf()
        }

        '\\', '/' -> {
            val newDir = turnDir(dir, char)
            val next = nextCoord(coord, newDir, grid)
            if (next != null) listOf(Pair(next, newDir)) else listOf()
        }

        '-', '|' -> {
            val splitDirs = splitDirs(dir, char)
            splitDirs.mapNotNull { d ->
                val next = nextCoord(coord, d, grid)
                if (next != null) Pair(next, d) else null
            }
        }

        else -> throw Exception("Unknown char $char")
    }
}

fun energize(grid: Grid, start: Pair<Coord, Dir>): Set<Coord> {
    val seenTiles = mutableSetOf<Pair<Coord, Dir>>()
    val tilesToProcess = mutableListOf(start)

    while (tilesToProcess.isNotEmpty()) {
        val tile = tilesToProcess.removeAt(0)
        seenTiles.add(tile)

        val nextTiles = getNextTiles(tile, grid)
        nextTiles.forEach { nextTile ->
            if (!seenTiles.contains(nextTile)) {
                tilesToProcess.add(nextTile)
            }
        }
    }

    return seenTiles.map { t -> t.first }.toSet()
}

fun main() {
    val exampleGrid = readLines("day16-example")
    val grid = readLines("day16")

    fun part1(grid: Grid): Int {
        val start = Pair(Pair(0, 0), Dir.East)
        return energize(grid, start).size
    }

    fun part2(grid: Grid): Int {
        val maxX = grid[0].length - 1
        val maxY = grid.size - 1;

        return arrayOf(
            Array(maxX) { x -> Pair(Pair(x, 0), Dir.South) },
            Array(maxX) { x -> Pair(Pair(x, maxY), Dir.North) },
            Array(maxY) { y -> Pair(Pair(0, y), Dir.East) },
            Array(maxY) { y -> Pair(Pair(maxX, y), Dir.West) },
        ).flatten().maxOf { start -> energize(grid, start).size }
    }

    val part1Example = part1(exampleGrid)
    check(part1Example == 46) { -> "Part 1 example failed: Expected 46, received $part1Example" };
    val part2Example = part2(exampleGrid)
    check(part2Example == 51) { -> "Part 2 example failed: Expected 51, received $part2Example" };

    val timePart1 = measureTime { part1(grid).println() }
    val timePart2 = measureTime { part2(grid).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}
