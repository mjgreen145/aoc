fun main() {
    val grid = readLines("day17").map { it.map { c -> c.toString().toInt() } }
    val lines = readLines("day17-debug")

    val dists: MutableMap<Coord, Pair<List<Coord>, Int>> = mutableMapOf()

    fun toCoords(str: String): List<Coord> {
        val trimmed = str.replace("[", "")
            .replace("]", "")
            .replace("(", "")
            .replace(")", "")

        if (trimmed == "") {
            return listOf()
        }
        return trimmed
            .split(", ")
            .chunked(2)
            .map { (x, y) -> Coord(x.toInt(), y.toInt()) }
    }

    lines.forEach { line ->
        val (coordStr, rest) = line.split("={")
        val (x, y) = coordStr.replace("(", "").replace(")", "").split(", ").map { it.toInt() }
        val coord = Coord(x, y)

        val regex = Regex("(\\[[^]]*]=\\d+)")
        val matches = regex.findAll(rest.replace("}", ""))

        val shortest: Pair<List<Coord>, Int> = matches.toList().map { match ->
            val (coordList, dist) = match.groupValues[1].split("=")
            Pair(toCoords(coordList), dist.toInt())
        }.minByOrNull { it.second }!!

        dists[coord] = shortest
    }

    var current = Coord(140, 140)
    var path = arrayOf(current)

    while(current != Coord(0, 0)) {
        val (pathSeg, dist) = dists[current]!!
        path = arrayOf(*pathSeg.toTypedArray(), *path)
        current = pathSeg.first()
    }

    writeVizDay17Debug(grid, path.toList())

    path.toList().sumOf { coord -> grid.get(coord) }.println()
}