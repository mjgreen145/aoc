package `2023`

import println
import readLines
import java.util.*
import kotlin.time.measureTime

enum class Pulse {
    High,
    Low
}

interface Module {
    fun send(from: String, pulse: Pulse): List<Triple<String, String, Pulse>> // From, To, Pulse
}

class FlipFlopModule(val name: String, val dests: List<String>) : Module, Cloneable {
    var on: Boolean = false
    override fun send(from: String, pulse: Pulse): List<Triple<String, String, Pulse>> {
        if (pulse == Pulse.Low) {
            this.on = !this.on
            val pulseToSend = if (this.on) Pulse.High else Pulse.Low
            return this.dests.map { Triple(this.name, it, pulseToSend) }
        }
        return listOf()
    }

    override fun equals(other: Any?): Boolean {
        return (other is FlipFlopModule && other.name == name && other.dests == dests && other.on == on)
    }

    override fun toString(): String {
        return "FlipFlop(name: $name, dests: $dests, on: $on)"
    }

    override fun clone(): FlipFlopModule {
        return FlipFlopModule(name, dests.toList())
    }
}

class ConjunctionModule(val name: String, val dests: List<String>) : Module, Cloneable {
    val recentPulses: MutableMap<String, Pulse> = mutableMapOf()
    override fun send(from: String, pulse: Pulse): List<Triple<String, String, Pulse>> {
        this.recentPulses[from] = pulse
        val pulseToSend = if (this.recentPulses.values.all { it == Pulse.High }) Pulse.Low else Pulse.High
        return this.dests.map { Triple(this.name, it, pulseToSend) }
    }

    fun addInput(input: String) {
        this.recentPulses[input] = Pulse.Low
    }

    override fun equals(other: Any?): Boolean {
        return (other is ConjunctionModule && other.name == name && other.dests == dests && other.recentPulses == recentPulses)
    }

    override fun toString(): String {
        return "Conjunction(name: $name, dests: $dests, recent: $recentPulses)"
    }

    override fun clone(): ConjunctionModule {
        val clone = ConjunctionModule(name, dests.toList())
        for (input in recentPulses.keys) {
            clone.addInput(input)
        }
        return clone
    }
}

class BroadcastModule(val name: String, val dests: List<String>) : Module, Cloneable {
    override fun send(from: String, pulse: Pulse): List<Triple<String, String, Pulse>> {
        return this.dests.map { Triple(this.name, it, pulse) }
    }

    override fun equals(other: Any?): Boolean {
        return (other is BroadcastModule && other.name == name && other.dests == dests)
    }

    override fun toString(): String {
        return "Broadcast(name: $name, dests: $dests)"
    }

    override fun clone(): Any {
        return BroadcastModule(name, dests.toList())
    }
}

class ButtonModule() : Module, Cloneable {
    override fun send(from: String, pulse: Pulse): List<Triple<String, String, Pulse>> {
        return listOf(Triple("button", "broadcaster", Pulse.Low))
    }

    override fun equals(other: Any?): Boolean {
        return (other is ButtonModule)
    }

    override fun toString(): String {
        return "Button()"
    }

    override fun clone(): Any {
        return ButtonModule()
    }
}

class SinkModule(val name: String) : Module, Cloneable {
    val pulses = mutableSetOf<Pulse>()
    override fun send(from: String, pulse: Pulse): List<Triple<String, String, Pulse>> {
        this.pulses.add(pulse)
        return listOf()
    }

    override fun equals(other: Any?): Boolean {
        return (other is SinkModule && other.name == name)
    }

    override fun toString(): String {
        return "Sink(name: $name)"
    }

    override fun clone(): Any {
        return SinkModule(name)
    }
}

fun parseInput(lines: List<String>): MutableMap<String, Module> {
    val modules = mutableMapOf<String, Module>()
    val allNames = mutableSetOf<String>()
    // Build modules
    lines.forEach { line ->
        val (fullName, allDests) = line.split(" -> ")
        val name = fullName.replace(Regex("[&%]"), "")
        val dests = allDests.split(", ")
        allNames.add(name)
        allNames.addAll(dests)

        val module = if (name == "broadcaster") BroadcastModule(name, dests)
        else if (fullName.startsWith('%')) FlipFlopModule(name, dests)
        else if (fullName.startsWith('&')) ConjunctionModule(name, dests)
        else throw Exception("unknown name $name")

        modules[name] = module
    }

    // Add inputs to Conjunctions
    lines.forEach { line ->
        val (fullName, allDests) = line.split(" -> ")
        val name = fullName.replace(Regex("[&%]"), "")
        val dests = allDests.split(", ")
        dests.forEach { dest ->
            val destModule = modules[dest]
            if (destModule is ConjunctionModule) {
                destModule.addInput(name)
            }
        }
    }

    // Add Sinks
    allNames.subtract(modules.keys).forEach { name ->
        modules[name] = SinkModule(name)
    }

    // Add a Button
    modules["button"] = ButtonModule()

    return modules
}

fun main() {
    val exampleLines = readLines("2023", "day20-example")
    val lines = readLines("2023", "day20")

    fun part1(lines: List<String>): Long {
        val modules = parseInput(lines)
        val queue: Queue<Triple<String, String, Pulse>> = LinkedList()
        var counts = mutableMapOf(Pair(Pulse.Low, 0L), Pair(Pulse.High, 0L))

        for (i in 1..1000) {
            queue.addAll(modules["button"]!!.send("", Pulse.Low))

            while (queue.isNotEmpty()) {
                val (from, to, pulse) = queue.remove()
                counts[pulse] = (counts[pulse]!! + 1)
                queue.addAll(modules[to]!!.send(from, pulse))
            }
        }
        return counts[Pulse.Low]!! * counts[Pulse.High]!!
    }

    fun part2(lines: List<String>): Long {
        var broadcasterDests = lines.find { it.startsWith("broadcaster") }!!.substringAfter(" -> ").split(", ")
        val allCycleLengths = mutableListOf<Long>()
        for (dest in broadcasterDests) {
            val modules = parseInput(lines)
            val queue: Queue<Triple<String, String, Pulse>> = LinkedList()
            var buttonPresses = 0L
            modules["broadcaster"] = BroadcastModule("broadcaster", listOf(dest))
            var reachedDest = false
            while (true) {
                queue.addAll(modules["button"]!!.send("", Pulse.Low))
                buttonPresses++

                while (queue.isNotEmpty()) {
                    val (from, to, pulse) = queue.remove()
                    if (to == "nc" && pulse == Pulse.High) {
                        allCycleLengths.add(buttonPresses)
                        reachedDest = true
                        break
                    }
                    queue.addAll(modules[to]!!.send(from, pulse))
                }
                if (reachedDest) {
                    break
                }
            }
        }

        return lcmMultiple(allCycleLengths)
    }

    val part1Example = part1(exampleLines)

    check(part1Example == 11687500L) { -> "Part 1 example failed: Expected 11687500, received $part1Example" };

    val timePart1 = measureTime { part1(lines).println() }
    val timePart2 = measureTime { part2(lines).println() }

    println("Part 1 took $timePart1")
    println("Part 2 took $timePart2")
}