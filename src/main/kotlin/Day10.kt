import kotlin.time.measureTime

enum class Dir {
    North,
    South,
    East,
    West
}

fun inverseDir(dir: Dir): Dir {
    return when (dir) {
        Dir.North -> Dir.South
        Dir.South -> Dir.North
        Dir.East -> Dir.West
        Dir.West -> Dir.East
    }
}

typealias Coord = Pair<Int, Int>

data class Pipe(val dirs: List<Dir>, val isStart: Boolean, val coord: Coord)

typealias Grid = List<List<Pipe?>>

fun parseGrid(lines: List<String>): Pair<Grid, Pipe?> {
    var startPipe: Pipe? = null;
    val grid = lines.mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            if (char == 'S') {
                val startDirs = mutableListOf<Dir>();
                if (y > 0 && listOf('|', '7', 'F').contains(lines[y - 1][x])) {
                    startDirs.add(Dir.North)
                }
                if (y < lines.size - 1 && listOf('|', 'L', 'J').contains(lines[y + 1][x])) {
                    startDirs.add(Dir.South)
                }
                if (x < line.length - 1 && listOf('-', '7', 'J').contains(lines[y][x + 1])) {
                    startDirs.add(Dir.East)
                }
                if (x > 0 && listOf('-', 'F', 'L').contains(lines[y][x - 1])) {
                    startDirs.add(Dir.West)
                }
                assert(startDirs.size == 2) { "Found ${startDirs.size} viable directions for the start" }
                startPipe = Pipe(startDirs, true, Pair(x, y))
                startPipe
            } else {
                when (char) {
                    '|' -> Pipe(listOf(Dir.North, Dir.South), false, Pair(x, y))
                    '-' -> Pipe(listOf(Dir.East, Dir.West), false, Pair(x, y))
                    'L' -> Pipe(listOf(Dir.North, Dir.East), false, Pair(x, y))
                    'J' -> Pipe(listOf(Dir.North, Dir.West), false, Pair(x, y))
                    '7' -> Pipe(listOf(Dir.South, Dir.West), false, Pair(x, y))
                    'F' -> Pipe(listOf(Dir.South, Dir.East), false, Pair(x, y))
                    else -> null
                }
            }
        }
    }
    return Pair(grid, startPipe);
}

tailrec fun walk(grid: Grid, current: Pipe, dirToWalk: Dir, prevPipes: MutableList<Pipe>): List<Pipe> {
    current.println()
    val (x, y) = current.coord;
    val nextPipe = when (dirToWalk) {
        Dir.North -> grid[y - 1][x]!!
        Dir.East -> grid[y][x + 1]!!
        Dir.South -> grid[y + 1][x]!!
        Dir.West -> grid[y][x - 1]!!
    }
    if (nextPipe.isStart) {
        return prevPipes
    }
    prevPipes.addLast(current)
    val nextDirToWalk = nextPipe.dirs.first { d -> d != inverseDir(dirToWalk) }
    return walk(grid, nextPipe, nextDirToWalk, prevPipes)
}

fun main() {
    val exampleLines = readLines("day10-example")
    val exampleLines2 = readLines("day10-example2")
    val lines = readLines("day10")

    fun part1(lines: List<String>): Int {
        val (grid, start) = parseGrid(lines)
        if (start == null) {
            throw Exception("Failed to find start point")
        }
        val dirToWalk = start.dirs.first()
        val path = walk(grid, start, dirToWalk, mutableListOf())
        return (path.size + 1) / 2
    }

    fun part2(lines: List<String>): Int {
        return 0
    }

    val part1Example = part1(exampleLines)
    check(part1Example == 8) { -> "Part 1 example failed: Expected 8, received $part1Example" };

    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")

    val part2Example = part2(exampleLines2)
    check(part2Example == 4) { -> "Part 2 example failed: Expected 4, received $part2Example" };

    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")
}