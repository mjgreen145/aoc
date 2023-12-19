import kotlin.time.measureTime

data class MachinePart(val x: Long, val m: Long, val a: Long, val s: Long, val sum: Long = x + m + a + s)
data class MachinePartRange(val x: LongRange, val m: LongRange, val a: LongRange, val s: LongRange)
data class Rule(
    val partPred: ((MachinePart) -> Boolean)? = null,
    val rangeTransform: ((MachinePartRange) -> Pair<MachinePartRange?, MachinePartRange?>)? = null,
    val dest: String
)

data class Workflow(val rules: List<Rule>)

typealias WorkflowMap = Map<String, Workflow>

fun parsePredicate(pred: String): (MachinePart) -> Boolean {
    val (_, prop, op, valueStr) = Regex("([a-z])([<>])(\\d+)").find(pred)!!.groupValues
    val value = valueStr.toLong()
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

fun splitRange(partRange: LongRange, op: String, value: Long): Pair<LongRange?, LongRange?> {
    return if (op == "<") {
        if (partRange.last < value) {
            Pair(partRange, null)
        } else if (partRange.first >= value) {
            Pair(null, partRange)
        } else Pair(partRange.first..<value, value..partRange.last)
    } else {
        if (partRange.first > value) {
            Pair(partRange, null)
        } else if (partRange.last <= value) {
            Pair(null, partRange)
        } else Pair((value + 1)..partRange.last, partRange.first..value)
    }
}

fun parseRangeTransform(pred: String): (MachinePartRange) -> Pair<MachinePartRange?, MachinePartRange?> {
    val (_, prop, op, valueStr) = Regex("([a-z])([<>])(\\d+)").find(pred)!!.groupValues
    val value = valueStr.toLong()

    return { partRange ->
        when (prop) {
            "x" -> {
                val (newRange, restRange) = splitRange(partRange.x, op, value)
                Pair(
                    if (newRange != null) partRange.copy(x = newRange) else null,
                    if (restRange != null) partRange.copy(x = restRange) else null
                )
            }

            "m" -> {
                val (newRange, restRange) = splitRange(partRange.m, op, value)
                Pair(
                    if (newRange != null) partRange.copy(m = newRange) else null,
                    if (restRange != null) partRange.copy(m = restRange) else null
                )
            }

            "a" -> {
                val (newRange, restRange) = splitRange(partRange.a, op, value)
                Pair(
                    if (newRange != null) partRange.copy(a = newRange) else null,
                    if (restRange != null) partRange.copy(a = restRange) else null
                )
            }

            "s" -> {
                val (newRange, restRange) = splitRange(partRange.s, op, value)
                Pair(
                    if (newRange != null) partRange.copy(s = newRange) else null,
                    if (restRange != null) partRange.copy(s = restRange) else null
                )
            }

            else -> throw Exception("Unknown prop $prop")
        }
    }


}

fun parseDay19(input: String): Pair<List<MachinePart>, WorkflowMap> {
    val (workflowsText, partsText) = input.split("\n\n").map { it.split("\n") }

    val parts = partsText.map { part ->
        val (_, x, m, a, s) = Regex("x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)").find(part)!!.groupValues
        MachinePart(x.toLong(), m.toLong(), a.toLong(), s.toLong())
    }

    val workflows = mutableMapOf<String, Workflow>()

    workflowsText.forEach { workflow ->
        val (_, name, rulesText) = Regex("([a-z]+)\\{(.+)}").find(workflow)!!.groupValues
        val rules = rulesText.split(",").map { rule ->
            if (rule.contains(':')) {
                Rule(
                    parsePredicate(rule.substringBefore(':')),
                    parseRangeTransform(rule.substringBefore(':')),
                    rule.substringAfter(':')
                )
            } else {
                Rule(dest = rule)
            }
        }
        workflows[name] = Workflow(rules)
    }

    return Pair(parts, workflows)
}

fun getDestination(part: MachinePart, workflow: Workflow): String {
    for (rule in workflow.rules) {
        val pred = rule.partPred
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

fun rangeLength(range: LongRange): Long {
    return (range.last - range.first) + 1
}

fun main() {
    val exampleLines = readInput("day19-example")
    val lines = readInput("day19")

    fun part1(input: String): Long {
        val (parts, workflows) = parseDay19(input)
        return parts.filter { part -> acceptPart(part, workflows) }.sumOf { it.sum }
    }

    fun part2(input: String): Long {
        val (_, workflows) = parseDay19(input)
        val initialPartRange = MachinePartRange(1L..4000L, 1L..4000L, 1L..4000L, 1L..4000L)

        val partsToProcess = mutableListOf(Pair(initialPartRange, workflows["in"]!!))
        val acceptedRanges = mutableListOf<MachinePartRange>()

        while (partsToProcess.isNotEmpty()) {
            val (partRange, workflow) = partsToProcess.removeFirst()
            val rule = workflow.rules.first()
            val transform = rule.rangeTransform
            if (transform != null) {
                val (newRange, restRange) = transform(partRange)
                if (newRange != null) {
                    if (rule.dest == "A") {
                        acceptedRanges.add(newRange)
                    } else if (rule.dest == "R") {
                        // Do nothing
                    } else {
                        partsToProcess.add(Pair(newRange, workflows[rule.dest]!!))
                    }
                }
                if (restRange != null) {
                    partsToProcess.add(Pair(restRange, Workflow(workflow.rules.drop(1))))
                }
            } else {
                if (rule.dest == "A") {
                    acceptedRanges.add(partRange)
                } else if (rule.dest == "R") {
                    // Do nothing
                } else {
                    partsToProcess.add(Pair(partRange, workflows[rule.dest]!!))
                }
            }
        }

        return acceptedRanges.sumOf { part ->
            rangeLength(part.x) * rangeLength(part.m) * rangeLength(part.a) * rangeLength(part.s)
        }
    }

    val part1Example = part1(exampleLines)
    val part2Example = part2(exampleLines)

    check(part1Example == 19114L) { -> "Part 1 example failed: Expected 19114, received $part1Example" };
    check(part2Example == 167409079868000L) { -> "Part 2 example failed: Expected 167409079868000, received $part2Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}