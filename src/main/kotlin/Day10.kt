import kotlin.time.measureTime

enum class Dir {
    North, South, East, West
}

enum class Turn {
    Left, Right
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
typealias Grid = List<List<Pipe>>

fun parseGrid(lines: List<String>): Pair<Grid, Pipe?> {
    var startPipe = Pipe(listOf(), true, Pair(0, 0));
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
                    else -> Pipe(listOf(), false, Pair(x, y))
                }
            }
        }
    }
    return Pair(grid, startPipe);
}

val clockwiseDirs = listOf(Dir.North, Dir.East, Dir.South, Dir.West)

tailrec fun walk(
    grid: Grid,
    current: Pipe,
    dirToWalk: Dir,
    prevPipes: MutableList<Pair<Pipe, Turn?>>
): List<Pair<Pipe, Turn?>> {
    val (x, y) = current.coord;
    val nextPipe = when (dirToWalk) {
        Dir.North -> grid[y - 1][x]
        Dir.East -> grid[y][x + 1]
        Dir.South -> grid[y + 1][x]
        Dir.West -> grid[y][x - 1]
    }
    val nextDirToWalk = nextPipe.dirs.first { d -> d != inverseDir(dirToWalk) }
    val turn = when ((clockwiseDirs.indexOf(nextDirToWalk) - clockwiseDirs.indexOf(dirToWalk)).mod(4)) {
        1, -3 -> Turn.Right
        -1, 3 -> Turn.Left
        else -> null
    }

    prevPipes.addLast(Pair(current, turn))
    if (nextPipe.isStart) {
        return prevPipes
    }
    return walk(grid, nextPipe, nextDirToWalk, prevPipes)
}

tailrec fun getContainedCoords(
    grid: Grid,
    path: List<Pair<Pipe, Turn?>>,
    pathCoords: Set<Coord>,
    pathIsClockwise: Boolean,
    currentDir: Dir,
    containedCoords: MutableSet<Coord>
): Set<Coord> {
    if (path.size == 0) {
        return containedCoords;
    }
    val (x, y) = path.first().first.coord;

    val internalCoords = when (Pair(currentDir, pathIsClockwise)) {
        Pair(Dir.North, true), Pair(Dir.South, false) -> grid[y].drop(x + 1)
        Pair(Dir.South, true), Pair(Dir.North, false) -> grid[y].take(x).reversed()
        Pair(Dir.East, true), Pair(Dir.West, false) -> grid.drop(y + 1).map { it[x] }
        Pair(Dir.West, true), Pair(Dir.East, false) -> grid.take(y).map { it[x] }.reversed()
        else -> throw Exception("Not possible")
    }
    val coordsToAdd = internalCoords.takeWhile { p -> !pathCoords.contains(p.coord) }
    coordsToAdd.forEach { pipe ->
        containedCoords.add(pipe.coord)
    }

    val nextDir = when (path.first().second) {
        Turn.Right -> clockwiseDirs[(clockwiseDirs.indexOf(currentDir) + 1).mod(4)]
        Turn.Left -> clockwiseDirs[(clockwiseDirs.indexOf(currentDir) - 1).mod(4)]
        null -> currentDir
    }
    return getContainedCoords(grid, path.drop(1), pathCoords, pathIsClockwise, nextDir, containedCoords)
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
        val (grid, start) = parseGrid(lines)
        if (start == null) {
            throw Exception("Failed to find start point")
        }
        val dirToWalk = start.dirs.first()
        val path = walk(grid, start, dirToWalk, mutableListOf())

        val rightTurns = path.filter { (_, turn) -> turn == Turn.Right }.size
        val leftTurns = path.filter { (_, turn) -> turn == Turn.Left }.size

        val pathIsClockwise = rightTurns > leftTurns
        val pathCoords = path.map { it.first.coord }.toSet()

        val containedCoords =
            getContainedCoords(grid, path, pathCoords, pathIsClockwise, dirToWalk, mutableSetOf())

        return containedCoords.size
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