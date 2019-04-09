import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

fun setupLogs(log: Logger) {
    log.level = Level.INFO
    val resourceDir = Paths.get(ClassLoader.getSystemClassLoader().getResource("/logs").toURI())
    if (!resourceDir.toFile().exists()) {
        resourceDir.toFile().mkdir()
    }
    val fileHandler = FileHandler("${resourceDir.toUri()}/${LocalDateTime.now()}.txt")
    fileHandler.formatter = SimpleFormatter()
    log.addHandler(fileHandler)
}
