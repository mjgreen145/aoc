package `2023`

import Coord
import java.io.File

fun writeVizDay17Debug(grid: HeatGrid, path: List<Coord>) {
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
          color: white;
          font-family: monospace;
      }
      
      .grid {
        position: absolute;
        transition: opacity;
        opacity: 0;
        background: black;
        padding: 10px;
      }
      
      .line {
        display: flex;
      }
      
      .cell {
        height: 18px;
        width: 10px;
      }
      
      .highlight {
        color: #df2308;
        text-shadow: 0 0 5px #df2308, 0 0 10px #df2308;
      }
  </style>
</head>
<body>
<br><br>
    ${
        grid.mapIndexed { y, line ->
            val htmlLine = line.mapIndexed { x, char ->
                val cssClass = if (path.contains(Coord(x, y))) "highlight" else ""
                "<div class=\"cell $cssClass\" data-coord=\"($x,$y)\">${grid.get(x, y)}</div>"
            }.joinToString("")
            "<div class=\"line\">${htmlLine}</div>"
        }.joinToString("")
    }
        </body >
        </html >
                """.trimIndent()

    File("./debug-day17-debug.html").writeText(html)
}
