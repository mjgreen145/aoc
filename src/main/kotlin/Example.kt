fun main() {
    val text = object {}.javaClass.getResourceAsStream("example-input.txt")?.bufferedReader()?.readText()

    val totals =
        text?.split("\n\n")?.map { lines ->
            lines.split("\n")
                .map { line -> line.toInt() }
                .reduce { a, b -> a + b }
        }

    println(totals?.max())
}