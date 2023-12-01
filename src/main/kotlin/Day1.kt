fun day1() {
    val text = object {}.javaClass.getResourceAsStream("day1.txt")?.bufferedReader()?.readText()

    // part 1
    val calibrationVals: List<Int>? = text?.split("\n")?.map { line ->
        val digits = line.filter { char -> char.isDigit() }
        "${digits.first()}${digits.last()}".toInt()
    }

    println(calibrationVals?.sum())

    // part 2
    val numberWords: Array<String> = arrayOf(
        "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
    )

    val regex = Regex("${numberWords.joinToString("|")}|\\d")

    val noOverlaps =
        text?.split("\n")?.map { line ->
            line.replace("oneight", "oneeight")
                .replace("threeight", "threeeight")
                .replace("fiveight", "fiveeight")
                .replace("nineight", "nineeight")
                .replace("twone", "twoone")
                .replace("sevenine", "sevennine")
                .replace("eightwo", "eighttwo")
                .replace("eighthree", "eightthree")
        }

    val calibrationVals2 = noOverlaps?.map { line ->
        val digits = regex.findAll(line).map { matchResult -> matchResult.value }.toList()
            .map { number -> if (numberWords.contains(number)) numberWords.indexOf(number) + 1 else number.toInt() }

        "${digits.first()}${digits.last()}".toInt()
    }

    println(calibrationVals2?.sum())
}