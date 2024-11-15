package `2023`

import Coord
import Dir
import Grid
import java.io.File

fun writeDebugDay16(grid: Grid, seenTilesList: List<Set<Pair<Coord, Dir>>>) {
    val html = """
<!DOCTYPE html>
<html lang='en'>
<head>
  <meta charset='UTF-8' />
  <style>
      body {
          margin: 0;
          padding: 0;
          width: 100%;
          height: 100vh;
          background-color: #000;
          color: #666;
          font-family: monospace;
      }
      
      .grid {
        position: absolute;
        transition: opacity;
        opacity: 0;
        background: black;
        padding: 10px;
      }
      
      .play .grid {
        opacity: 1;
      }
      
      .line {
        display: flex;
      }
      
      .cell {
        height: 18px;
        width: 10px;
      }
      
      .lit {
        color: #ffff66;
        text-shadow: 0 0 5px #ffff66, 0 0 10px #ffff66;
      }
      
      .dot::after { 
        content: ".";
      }
      .two::after { 
        content: "2";
      }
      .three::after { 
        content: "3";
      }
      .four::after { 
        content: "4";
      }
      .north::after { 
        content: "^";
      }
      .south::after { 
        content: "v";
      }
      .east::after { 
        content: ">";
      }
      .west::after { 
        content: "<";
      }
      .forward_slash::after {
        content: "/";
      }
      .back_slash::after {
        content: "\\";
      }
      .horizontal_split::after {
        content: "-";
      }
      .vertical_split::after {
        content: "|";
      }
  </style>
</head>
<body>
    <button onClick="document.body.classList.add('play')">Go!</button>
    ${
        seenTilesList.mapIndexed { i, seenTiles ->
            val delay = 0.25 * i;
            "<div class=\"grid\" style=\"transition-delay: ${delay}s;\">${generateGridHTML(grid, seenTiles)}</div>"
        }.joinToString("")
    }
</body>
</html>
    """.trimIndent()

    File("./debug-day16.html").writeText(html)
}

fun generateGridHTML(grid: Grid, seenTiles: Set<Pair<Coord, Dir>>): String {
    val seenCoords = seenTiles.map { (coord, _) -> coord }.toSet()
    return grid.mapIndexed { y, line ->
        val pipes = line.mapIndexed { x, char ->
            val isLit = seenCoords.contains(Pair(x, y))
            val tilesForCoord = seenTiles.filter { (c, _) -> c == Pair(x, y) }
            val charToWrite = if (char == '.') {
                when (tilesForCoord.size) {
                    0 -> '.'
                    1 -> charForDir(tilesForCoord.first().second)
                    else -> tilesForCoord.size.toString().first()
                }
            } else char

            val classes = listOfNotNull(
                "cell",
                charClass(charToWrite),
                if (isLit) "lit" else null,
            )
            "<div class=\"${classes.joinToString(" ")}\" data-coord=\"($x,$y)\"></div>"
        }.joinToString("")
        "<div class=\"line\">${pipes}</div>"
    }.joinToString("")
}

fun charForDir(dir: Dir): Char {
    return when (dir) {
        Dir.North -> '^'
        Dir.South -> 'v'
        Dir.East -> '>'
        Dir.West -> '<'
    }
}

fun charClass(char: Char): String? {
    return when (char) {
        '.' -> "dot"
        '/' -> "forward_slash"
        '\\' -> "back_slash"
        '-' -> "horizontal_split"
        '|' -> "vertical_split"
        '>' -> "east"
        '<' -> "west"
        '^' -> "north"
        'v' -> "south"
        '2' -> "two"
        '3' -> "three"
        '4' -> "four"
        else -> null
    }
}