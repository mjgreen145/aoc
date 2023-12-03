import kotlin.io.path.Path
import kotlin.io.path.readLines

fun Any?.println() = println(this)

fun readInput(name: String) = Path("src/main/resources/$name.txt").readLines()