package `2023`

import Coord
import java.io.File

fun writeDebugDay17(grid: HeatGrid, fastestRoutes: Map<Coord, Pair<Array<Coord>, Int>>) {
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
      
      .play .grid {
        opacity: 1;
      }
      
      .line {
        display: flex;
      }
      
      .cell {
        height: 18px;
        width: 30px;
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
            val pipes = line.mapIndexed { x, char ->
                "<div class=\"cell\" data-coord=\"($x,$y)\">${grid.get(x, y)}</div>"
            }.joinToString("")
            "<div class=\"line\">${pipes}</div>"
        }.joinToString("")
    }
    <br><br>
    ${
        grid.mapIndexed { y, line ->
            val pipes = line.mapIndexed { x, char ->
                val (path, total) = fastestRoutes[Pair(x, y)]!!
                "<div class=\"cell\" data-coord=\"($x,$y)\" onclick=\"highlight(${pathToJson(path)})\">$total</div>"
            }.joinToString("")
            "<div class=\"line\">${pipes}</div>"
        }.joinToString("")
    }
    <script>
        function highlight(coords) {
            Array.from(document.querySelectorAll(".cell")).forEach(c => c.classList.remove('highlight'));
            coords.map(c => { 
                document.querySelector(`[data-coord="(${"\${c[0]},\${c[1]}"})"]`).classList.add('highlight');
            });
        }
    
    </script>
        </body >
        </html >
                """.trimIndent()

    File("./debug-day17.html").writeText(html)
}

fun pathToJson(path: Array<Coord>): String {
    return path.toList().map { c -> "[${c.first}, ${c.second}]" }.toString()
}