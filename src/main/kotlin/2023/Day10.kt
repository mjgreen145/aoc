package `2023`

import Coord
import Dir
import println
import readLines
import java.io.File
import kotlin.time.measureTime

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

data class Pipe(val dirs: List<Dir>, val isStart: Boolean, val coord: Coord, val char: Char)
typealias PipeGrid = List<List<Pipe>>

fun parseGrid(lines: List<String>): Pair<PipeGrid, Pipe?> {
    var startPipe = Pipe(listOf(), true, Pair(0, 0), 'S');
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
                startPipe = Pipe(startDirs, true, Pair(x, y), char)
                startPipe
            } else {
                when (char) {
                    '|' -> Pipe(listOf(Dir.North, Dir.South), false, Pair(x, y), char)
                    '-' -> Pipe(listOf(Dir.East, Dir.West), false, Pair(x, y), char)
                    'L' -> Pipe(listOf(Dir.North, Dir.East), false, Pair(x, y), char)
                    'J' -> Pipe(listOf(Dir.North, Dir.West), false, Pair(x, y), char)
                    '7' -> Pipe(listOf(Dir.South, Dir.West), false, Pair(x, y), char)
                    'F' -> Pipe(listOf(Dir.South, Dir.East), false, Pair(x, y), char)
                    else -> Pipe(listOf(), false, Pair(x, y), char)
                }
            }
        }
    }
    return Pair(grid, startPipe);
}

val clockwiseDirs = listOf(Dir.North, Dir.East, Dir.South, Dir.West)

tailrec fun walk(
    grid: PipeGrid,
    current: Pipe,
    currentDir: Dir,
    prevPipes: MutableList<Pair<Pipe, Turn?>>
): List<Pair<Pipe, Turn?>> {
    val (x, y) = current.coord;
    val nextDir = current.dirs.first { d -> d != inverseDir(currentDir) }
    val nextPipe = when (nextDir) {
        Dir.North -> grid[y - 1][x]
        Dir.East -> grid[y][x + 1]
        Dir.South -> grid[y + 1][x]
        Dir.West -> grid[y][x - 1]
    }
    val turn = when ((clockwiseDirs.indexOf(nextDir) - clockwiseDirs.indexOf(currentDir)).mod(4)) {
        1, -3 -> Turn.Right
        -1, 3 -> Turn.Left
        else -> null
    }

    prevPipes.addLast(Pair(current, turn))
    if (nextPipe.isStart) {
        return prevPipes
    }
    return walk(grid, nextPipe, nextDir, prevPipes)
}

fun addContainedCoords(
    grid: PipeGrid,
    path: List<Pair<Pipe, Turn?>>,
    pathCoords: Set<Coord>,
    pathIsClockwise: Boolean,
    currentDir: Dir,
    containedCoords: MutableSet<Coord>
) {
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
}

tailrec fun getContainedCoords(
    grid: PipeGrid,
    path: List<Pair<Pipe, Turn?>>,
    pathCoords: Set<Coord>,
    pathIsClockwise: Boolean,
    currentDir: Dir,
    containedCoords: MutableSet<Coord>
): Set<Coord> {
    if (path.isEmpty()) {
        return containedCoords;
    }

    addContainedCoords(grid, path, pathCoords, pathIsClockwise, currentDir, containedCoords);

    val nextDir = when (path.first().second) {
        Turn.Right -> clockwiseDirs[(clockwiseDirs.indexOf(currentDir) + 1).mod(4)]
        Turn.Left -> clockwiseDirs[(clockwiseDirs.indexOf(currentDir) - 1).mod(4)]
        null -> currentDir
    }

    if (nextDir != currentDir) {
        addContainedCoords(grid, path, pathCoords, pathIsClockwise, nextDir, containedCoords);
    }
    return getContainedCoords(grid, path.drop(1), pathCoords, pathIsClockwise, nextDir, containedCoords)
}

fun main() {
    val exampleLines = readLines("2023", "day10-example")
    val exampleLines2 = readLines("2023", "day10-example2")
    val lines = readLines("2023", "day10")

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

        writeDebugDay10(grid, pathCoords, containedCoords);

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

fun writeDebugDay10(grid: PipeGrid, pathCoords: Set<Coord>, containedCoords: Set<Coord>) {
    val html = """
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8' />
  <style>
      body {
          margin: 0;
          padding: 0;
          width: 100%;
          height: 100vh;
          background-color: #000;
          color: white;
          font-family: monospace;
      }
      
      .line {
        display: flex;
      }
      
      .path {
        background-color: orangered;
      }
      
      .contained {
        background-color: lightgoldenrodyellow;
        color: black;
      }
      
      .start {
        background-color: green;
      }
  </style>
</head>
<body>
    ${
        grid.joinToString("") { line ->
            val pipes = line.joinToString("") { pipe ->
                val classes = listOfNotNull(
                    "pipe",
                    if (pathCoords.contains(pipe.coord)) "path" else null,
                    if (containedCoords.contains(pipe.coord)) "contained" else null,
                    if (pipe.isStart) "start" else null,
                )
                "<div class=\"${classes.joinToString(" ")}\" data-coord=\"(${pipe.coord.first},${pipe.coord.second})\">${pipe.char}</div>"
            }
            "<div class=\"line\">${pipes}</div>"
        }
    }
</body>
</html>
    """.trimIndent()

    File("./debug-day10.html").writeText(html)
}