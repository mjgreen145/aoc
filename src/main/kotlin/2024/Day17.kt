package `2024`

import println
import readInput
import kotlin.math.pow
import kotlin.time.measureTime

fun main() {
    val exampleInput = readInput("2024", "day17-example")
    val exampleInput2 = readInput("2024", "day17-example2")
    val input = readInput("2024", "day17")

    fun div(n: Long, d: Long): Long {
        return n / (2.0.pow(d.toDouble())).toLong()
    }

    fun run(instructions: List<Long>, aReg: Long): List<Long> {
        var a = aReg
        var b = 0L
        var c = 0L

        fun combo(operand: Long): Long {
            return when (operand) {
                in 0L..3L -> operand
                4L -> a
                5L -> b
                6L -> c
                7L -> 7L
                else -> throw IllegalArgumentException("Operand: $operand")
            }
        }

        val out = mutableListOf<Long>()
        var p = 0;
        while (p < instructions.size) {
            val (opcode, operand) = instructions.subList(p, p + 2)
            val comboOperand = combo(operand)

            when (opcode) {
                0L -> div(a, comboOperand).also { a = it }
                1L -> (b xor operand).also { b = it }
                2L -> (comboOperand % 8).also { b = it }
                3L -> (if (a == 0L) p else operand - 2).also { p = it.toInt() }
                4L -> (b xor c).also { b = it }
                5L -> (comboOperand % 8).also { out.add(it) }
                6L -> div(a, comboOperand).also { b = it }
                7L -> div(a, comboOperand).also { c = it }
            }

            p += 2
        }

        return out
    }

    fun pow(base: Long, exp: Int): Long {
        return base.toDouble().pow(exp).toLong();
    }

    fun part1(input: String): String {
        val a = Regex("[ABC]: (\\d+)").find(input)!!.groupValues[1].toLong()
        val instructions = input.substringAfter("Program: ").split(",").map { it.toLong() }
        return run(instructions, a).joinToString(",")
    }

    fun part2(input: String): Long {
        val instructions = input.substringAfter("Program: ").split(",").map { it.toLong() }
        val maxPower = instructions.size - 1

        fun solveFor(baseA: Long, index: Int): Long? {
            for (i in 0..7) {
                val a = baseA + pow(8, maxPower - index) * i
                if (run(instructions, a).reversed()[index].toInt() == instructions.reversed()[index].toInt()) {
                    if (index == maxPower) {
                        return a
                    } else {
                        val solution = solveFor(a, index + 1)
                        if (solution != null) return solution
                    }
                }
            }
            return null
        }

        return solveFor(pow(8L, maxPower), 0) ?: 0;
    }

    val part1Example = part1(exampleInput)
    val part2Example = part2(exampleInput2)

    check(part1Example == "4,6,3,5,6,3,5,2,1,0") { -> "Part 1 example failed: Expected 4,6,3,5,6,3,5,2,1,0, received $part1Example" };
    check(part2Example == 117440L) { -> "Part 2 example failed: Expected 117440, received $part2Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}