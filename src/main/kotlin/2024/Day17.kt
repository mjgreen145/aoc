package `2024`

import println
import readInput
import kotlin.math.pow
import kotlin.time.measureTime

fun main() {
    val exampleInput = readInput("2024", "day17-example")
    val exampleInput2 = readInput("2024", "day17-example2")
    val input = readInput("2024", "day17")

    fun div(n: Int, d: Int): Int {
        return n / (2.0.pow(d)).toInt()
    }

    fun run(instructions: List<Int>, aReg: Int): List<Int> {
        var a = aReg
        var b = 0
        var c = 0

        fun combo(operand: Int): Int {
            return when (operand) {
                in 0..3 -> operand
                4 -> a
                5 -> b
                6 -> c
                7 -> 7
                else -> throw IllegalArgumentException("Operand: $operand")
            }
        }

        val out = mutableListOf<Int>()
        var p = 0;
        while (p < instructions.size) {
            val (opcode, operand) = instructions.subList(p, p + 2)
            val comboOperand = combo(operand)

            when (opcode) {
                0 -> div(a, comboOperand).also { a = it }
                1 -> (b xor operand).also { b = it }
                2 -> (comboOperand % 8).also { b = it }
                3 -> (if (a == 0) p else operand - 2).also { p = it }
                4 -> (b xor c).also { b = it }
                5 -> (comboOperand % 8).also { out.add(it) }
                6 -> div(a, comboOperand).also { b = it }
                7 -> div(a, comboOperand).also { c = it }
            }

            p += 2
        }

        return out
    }

    fun part1(input: String): String {
        var a = Regex("[ABC]: (\\d+)").find(input)!!.groupValues[1].toInt()
        val instructions = input.substringAfter("Program: ").split(",").map { it.toInt() }
        return run(instructions, a).joinToString(",")
    }

    fun part2(input: String): Int {
        val instructions = input.substringAfter("Program: ").split(",").map { it.toInt() }
        var a = 0

        while(true) {
            if (a % 1000000 == 0) {
                a.println()
            }
            val out = run(instructions, a)
            if (out.joinToString("") == instructions.joinToString("")) {
                break
            }
            a++
        }
        return a
    }

    val part1Example = part1(exampleInput)
    val part2Example = part2(exampleInput2)

    check(part1Example == "4,6,3,5,6,3,5,2,1,0") { -> "Part 1 example failed: Expected 4,6,3,5,6,3,5,2,1,0, received $part1Example" };
    check(part2Example == 117440) { -> "Part 2 example failed: Expected 117440, received $part2Example" };

    val timePart1 = measureTime { part1(input).println() }
    val timePart2 = measureTime { part2(input).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}