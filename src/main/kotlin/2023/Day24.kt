package `2023`

import println
import readLines
import kotlin.time.measureTime

typealias Vector = Triple<Double, Double, Double>

// Representing the formula ax + by = c
data class LineFormula(val a: Double, val b: Double, val c: Double)
data class Hailstone(
    val input: String,
    val formula2D: LineFormula,
    val start: Triple<Double, Double, Double>,
    val velocity: Vector
)

// Representing the formula ax + by + cz = d
data class PlaneFormula(val a: Double, val b: Double, val c: Double, val d: Double)

fun toHailstone(line: String): Hailstone {
    val (start, vector) = line.split(" @ ")
    val (x, y, z) = start.split(", ").map { it.toDouble() }
    val (vx, vy, vz) = vector.split(", ").map { it.toDouble() }

    // y = mx + c
    val m = vy / vx
    val c = y - m * x
    return Hailstone(
        line,
        LineFormula(-m, 1.0, c),
        Triple(x, y, z),
        Triple(vx, vy, vz),
    )
}

typealias Matrix = Array<Array<Double>>

fun matrixInverse(matrix: Matrix): Matrix? {
    check(matrix.size == 2 && matrix[0].size == 2 && matrix[1].size == 2)
    val (a, b) = matrix[0]
    val (c, d) = matrix[1]

    val determinant = a * b - c * d
    if (determinant == 0.0) {
        return null
    }
    val cooeficient = 1 / determinant

    return arrayOf(
        arrayOf(cooeficient * d, cooeficient * -b),
        arrayOf(cooeficient * -c, cooeficient * a),
    )
}

fun vectorMultiply(u: Array<Double>, v: Array<Double>): Double {
    check(u.size == v.size)

    return (v zip u).sumOf { (x, y) -> x * y }
}

fun matrixMultiply(A: Matrix, B: Matrix): Matrix {
    return A.map { aRow ->
        B[0].mapIndexed { i, _ ->
            vectorMultiply(aRow, B.map { it[i] }.toTypedArray())
        }.toTypedArray()
    }.toTypedArray()
}

fun getIntersectionPoint(A: LineFormula, B: LineFormula): Pair<Double, Double>? {
    val matrix: Matrix = arrayOf(arrayOf(A.a, A.b), arrayOf(B.a, B.b))
    val vector: Matrix = arrayOf(arrayOf(A.c), arrayOf(B.c))
    val inverse = matrixInverse(matrix) ?: return null

    val intersectionVector = matrixMultiply(inverse, vector)
    return Pair(intersectionVector[0][0], intersectionVector[1][0])
}

fun isInTheFuture2D(hailstone: Hailstone, point: Pair<Double, Double>): Boolean {
    return if (hailstone.velocity.first > 0) (hailstone.start.first < point.first) else (hailstone.start.first > point.first)
}

fun findParallel3D(hailstones: List<Hailstone>): Pair<Hailstone, Hailstone> {
    hailstones.forEachIndexed { i, h1 ->
        hailstones.drop(i + 1).forEach { h2 ->
            val v1 = h1.velocity
            val v2 = h2.velocity
            val ratioX = v1.first / v2.first
            val ratioY = v1.second / v2.second
            val ratioZ = v1.third / v2.third
            if (ratioX == ratioY && ratioX == ratioZ) {
                return Pair(h1, h2)
            }
        }
    }
    throw Exception("No hailstones are in parallel")
}

fun crossProduct(v1: Vector, v2: Vector): Vector {
    val (a1, a2, a3) = v1
    val (b1, b2, b3) = v2
    return Triple(
        a2 * b3 - a3 * b2,
        a1 * b3 - a3 * b1,
        a1 * b2 - a2 * b1,
    )
}

fun planeForParallelHailstones(h1: Hailstone, h2: Hailstone): PlaneFormula {
    val (a, b, c) = crossProduct(h1.velocity, h2.velocity)
    val (x, y, z) = h1.start

    val d = a * x + b * y + c * z

    return PlaneFormula(a, b, c, d)
}

fun main() {
    val exampleLines = readLines("2023", "day24-example")
    val lines = readLines("2023", "day24")

    fun part1(lines: List<String>, lowerBound: Long, upperBound: Long): Int {
        val range = (lowerBound.toDouble()..upperBound.toDouble())
        val hailstones = lines.map(::toHailstone)

        var hits = 0

        hailstones.forEachIndexed { i, h1 ->
            hailstones.drop(i + 1).forEach { h2 ->
                val intersect = getIntersectionPoint(h1.formula2D, h2.formula2D)
                if (intersect != null &&
                    range.contains(intersect.first) &&
                    range.contains(intersect.second) &&
                    isInTheFuture2D(h1, intersect) &&
                    isInTheFuture2D(h2, intersect)
                ) {
                    hits++
                }
            }
        }
        return hits
    }

    fun part2(lines: List<String>): Int {
        val hailstones = lines.map(::toHailstone)
        val (h1, h2) = findParallel3D(hailstones)

        val plane = planeForParallelHailstones(h1, h2)

        return 0
    }

    val part1Example = part1(exampleLines, 7, 27)
    val part2Example = part2(exampleLines)

    check(part1Example == 2) { -> "Part 1 example failed: Expected 2, received $part1Example" };
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    val timePart1 = measureTime { part1(lines, 200000000000000, 400000000000000).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}