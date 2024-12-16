package `2024`

import Coord
import Dir
import Grid
import Turn
import findChar
import get
import move
import readLines
import println
import turn
import kotlin.time.measureTime

fun main() {
    val exampleInput1 = readLines("2024", "day16-example1")
    val exampleInput2 = readLines("2024", "day16-example2")
    val input = readLines("2024", "day16")

    fun bestPathsFrom(grid: Grid, start: Pair<Coord, Dir>, end: Coord): Pair<Int, List<Set<Coord>>> {
        val toCheck = mutableListOf(Triple(start, 0, setOf(start.first)))
        val checked = mutableMapOf<Pair<Coord, Dir>, Int>()
        var bestScore = Int.MAX_VALUE
        val bestPaths = mutableListOf<Set<Coord>>()

        while (toCheck.isNotEmpty()) {
            val (pos, score, path) = toCheck.removeFirst()
            val (coord, dir) = pos
            if (coord == end) {
                if (score < bestScore) {
                    bestPaths.removeAll { _ -> true }
                    bestScore = score
                    bestPaths.add(path)
                } else if (score == bestScore) {
                    bestPaths.add(path)
                }
                continue
            }
            if (checked.getOrDefault(pos, Int.MAX_VALUE) < score) {
                continue
            }
            checked[pos] = score

            listOf(Pair(coord.move(dir), dir), Pair(coord, dir.turn(Turn.Left)), Pair(coord, dir.turn(Turn.Right)))
                .filter { (_, d) -> grid.get(coord.move(d)) != '#' }
                .forEach { p -> toCheck.add(Triple(p, score + if (p.second == dir) 1 else 1000, path + p.first)) }
        }

        return Pair(bestScore, bestPaths)
    }

    fun part1(grid: Grid): Int {
        val s = grid.findChar('S')
        val end = grid.findChar('E')
        return bestPathsFrom(grid, Pair(s, Dir.East), end).first
    }

    fun part2(grid: Grid): Int {
        val s = grid.findChar('S')
        val end = grid.findChar('E')
        return bestPathsFrom(grid, Pair(s, Dir.East), end).second.flatten().toSet().size
    }

    val part1Example1 = part1(exampleInput1)
    val part1Example2 = part1(exampleInput2)
    val part2Example1 = part2(exampleInput1)
    val part2Example2 = part2(exampleInput2)

    check(part1Example1 == 7036) { -> "Part 1 example failed: Expected 7036, received $part1Example1" };
    check(part1Example2 == 11048) { -> "Part 1 example failed: Expected 11048, received $part1Example2" };
    check(part2Example1 == 45) { -> "Part 2 example failed: Expected 45, received $part2Example1" };
    check(part2Example2 == 64) { -> "Part 2 example failed: Expected 64, received $part2Example2" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}