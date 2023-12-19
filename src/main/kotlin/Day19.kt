import kotlin.time.measureTime

data class MachinePart(val x: Int, val m: Int, val a: Int, val s: Int, val sum: Int = x + m + a + s)
data class Rule(val predicate: ((MachinePart) -> Boolean)?, val dest: String)
data class Workflow(val name: String, val rules: List<Rule>)

typealias WorkflowMap = Map<String, Workflow>

fun parsePredicate(pred: String): (MachinePart) -> Boolean {
    val (_, prop, op, valueStr) = Regex("([a-z])([<>])(\\d+)").find(pred)!!.groupValues
    val value = valueStr.toInt()
    return { part: MachinePart ->
        when (prop) {
            "x" -> if (op == "<") part.x < value else part.x > value
            "m" -> if (op == "<") part.m < value else part.m > value
            "a" -> if (op == "<") part.a < value else part.a > value
            "s" -> if (op == "<") part.s < value else part.s > value
            else -> throw Exception("Unknown prop $prop")
        }

    }
}

fun parseDay19(input: String): Pair<List<MachinePart>, WorkflowMap> {
    val (workflowsText, partsText) = input.split("\n\n").map { it.split("\n") }

    val parts = partsText.map { part ->
        val (_, x, m, a, s) = Regex("x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)").find(part)!!.groupValues
        MachinePart(x.toInt(), m.toInt(), a.toInt(), s.toInt())
    }

    val workflows = mutableMapOf<String, Workflow>()

    workflowsText.forEach { workflow ->
        val (_, name, rulesText) = Regex("([a-z]+)\\{(.+)}").find(workflow)!!.groupValues
        val rules = rulesText.split(",").map { rule ->
            if (rule.contains(':')) {
                Rule(parsePredicate(rule.substringBefore(':')), rule.substringAfter(':'))
            } else {
                Rule(null, rule)
            }
        }
        workflows[name] = Workflow(name, rules)
    }

    return Pair(parts, workflows)
}

fun getDestination(part: MachinePart, workflow: Workflow): String {
    for (rule in workflow.rules) {
        val pred = rule.predicate
        if (pred == null || pred(part)) {
            return rule.dest
        }
    }
    throw Exception("Failed to find dest for workflow")
}

tailrec fun acceptPart(part: MachinePart, workflows: WorkflowMap, currentWorkflow: String = "in"): Boolean {
    val workflow = workflows[currentWorkflow]!!
    val dest = getDestination(part, workflow)
    if (dest == "A") return true
    if (dest == "R") return false
    return acceptPart(part, workflows, dest)
}

fun main() {
    val exampleLines = readInput("day19-example")
    val lines = readInput("day19")

    fun part1(input: String): Int {
        val (parts, workflows) = parseDay19(input)
        return parts.filter { part -> acceptPart(part, workflows) }.sumOf { it.sum }
    }

    fun part2(input: String): Int {
        return 0
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 19114) { -> "Part 1 example failed: Expected 19114, received $part1Example" };
    check(part2Example == 0) { -> "Part 2 example failed: Expected 0, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}