import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

fun Any?.println() = println(this)

fun readInput(name: String) = Path("src/main/resources/$name.txt").readText();

fun readLines(name: String) = Path("src/main/resources/$name.txt").readLines()