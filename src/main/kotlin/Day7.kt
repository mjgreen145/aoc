import kotlin.math.pow
import kotlin.time.measureTime

enum class HandType {
    FiveOfAKind,
    FourOfAKind,
    FullHouse,
    ThreeOfAKind,
    TwoPair,
    OnePair,
    HighCard,
}

val cardOrder: List<Char> = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
val cardOrderWithJoker: List<Char> = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')

data class Hand(val cards: String, val type: HandType, val bid: Int)

fun classify(cards: String): HandType {
    val charsMap = mutableMapOf<Char, Int>()
    cards.forEach { charsMap[it] = charsMap.getOrDefault(it, 0) + 1 }

    return when (charsMap.values.sorted()) {
        listOf(5) -> HandType.FiveOfAKind
        listOf(1, 4) -> HandType.FourOfAKind
        listOf(2, 3) -> HandType.FullHouse
        listOf(1, 1, 3) -> HandType.ThreeOfAKind
        listOf(1, 2, 2) -> HandType.TwoPair
        listOf(1, 1, 1, 2) -> HandType.OnePair
        else -> HandType.HighCard
    }
}

fun classifyWithJoker(cards: String): HandType {
    val charsMap = mutableMapOf<Char, Int>()
    val cardsWithoutJokers = cards.filter { c -> c != 'J' }

    cardsWithoutJokers.forEach { charsMap[it] = charsMap.getOrDefault(it, 0) + 1 }

    return when (charsMap.values.sorted()) {
        // 0 non-jokers, 5 jokers
        emptyList<Int>() -> HandType.FiveOfAKind
        // 1 non-joker, 4 jokers
        listOf(1) -> HandType.FiveOfAKind
        // 2 non-jokers, 3 jokers
        listOf(2) -> HandType.FiveOfAKind
        listOf(1, 1) -> HandType.FourOfAKind
        // 3 non-jokers, 2 jokers
        listOf(3) -> HandType.FiveOfAKind
        listOf(1, 2) -> HandType.FourOfAKind
        listOf(1, 1, 1) -> HandType.ThreeOfAKind
        // 4 non-jokers, 1 joker
        listOf(4) -> HandType.FiveOfAKind
        listOf(1, 3) -> HandType.FourOfAKind
        listOf(2, 2) -> HandType.FullHouse
        listOf(1, 1, 2) -> HandType.ThreeOfAKind
        listOf(1, 1, 1, 1) -> HandType.OnePair
        // 5 non-jokers, 0 jokers
        listOf(5) -> HandType.FiveOfAKind
        listOf(1, 4) -> HandType.FourOfAKind
        listOf(2, 3) -> HandType.FullHouse
        listOf(1, 1, 3) -> HandType.ThreeOfAKind
        listOf(1, 2, 2) -> HandType.TwoPair
        listOf(1, 1, 1, 2) -> HandType.OnePair
        else -> HandType.HighCard
    }
}

fun scoreHands(lines: List<String>, cardOrder: List<Char>, classifier: (cards: String) -> HandType): Int {
    val hands = lines.map { line ->
        val (cards, bid) = line.split(" ")
        Hand(cards, classifier(cards), bid.toInt())
    }
    val sorted =
        hands.sortedWith(
            compareBy<Hand> { HandType.entries.indexOf(it.type) }
                .thenBy {
                    it.cards.mapIndexed { i, card: Char ->
                        cardOrder.indexOf(card) * cardOrder.size.toDouble().pow(5 - i)
                    }.sum()
                }
        ).reversed()

    return sorted.mapIndexed { i, hand -> hand.bid * (i + 1) }.sum()
}

fun main() {
    val exampleLines = readLines("day7-example")
    val lines = readLines("day7")

    fun part1(lines: List<String>): Int {
        return scoreHands(lines, cardOrder, ::classify);
    }

    fun part2(lines: List<String>): Int {
        return scoreHands(lines, cardOrderWithJoker, ::classifyWithJoker)
    }

    check(part1(exampleLines) == 6440) { -> "Part 1 example failed" };
    val timePart1 = measureTime { part1(lines).println() }
    println("Part 1 took $timePart1")

    check(part2(exampleLines) == 5905) { -> "Part 2 example failed" };
    val timePart2 = measureTime { part2(lines).println() }
    println("Part 2 took $timePart2")
}