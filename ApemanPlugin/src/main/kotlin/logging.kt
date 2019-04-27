import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

fun setupLogs(log: Logger) {
    log.level = Level.INFO

//    val logsUri = "/home/snyss/Prog/mm/diploma/main/logs" //ExtractionCandidate::class.java.getResource("../").toURI().toString() + "/logs"
//    val resourceDir = Paths.get(logsUri)
//    if (!resourceDir.toFile().exists()) {
//        resourceDir.toFile().mkdir()
//    }
//    val logFile = "$logsUri/${LocalDateTime.now()}.txt"
//    val fileHandler = FileHandler(logFile)
//    fileHandler.formatter = SimpleFormatter()
//    log.addHandler(fileHandler)
}

fun handleError(e: Error) {
    print(e)
    e.printStackTrace()
}

fun handleException(e: Exception) {
    print(e)
    e.printStackTrace()
}