package proof_of_concept

import com.intellij.openapi.application.ApplicationStarter
import java.time.LocalDateTime
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import kotlin.system.exitProcess

class ProofOfConceptLauncher : ApplicationStarter {

    val log = Logger.getGlobal()
    override fun getCommandName() = "proof-launcher"

    override fun premain(args: Array<out String>?) {}

    override fun main(args: Array<String>) {
        log.level = Level.INFO
        val fileHandler = FileHandler("/home/snyss/Prog/mm/diploma/main/logs_" + LocalDateTime.now() + ".txt")
        fileHandler.formatter = SimpleFormatter()
        log.addHandler(fileHandler)

//        val subjectDir = "/home/snyss/Prog/mm/diploma/main/apeman/GemsDataset/subjects/"
        val subjectDir = "/home/snyss/Prog/mm/diploma/gems_datasets/subjects/"
        val results = arrayListOf<Results>()

        try {
//            val junit = OneProjectAnalyzer(subjectDir + "junit3.8")
//            results.addAll(junit.analyze())
//
//            val jHotDraw = OneProjectAnalyzer(subjectDir + "JHotDraw5.2")
//            results.addAll(jHotDraw.analyze())
//
            val myWebMarker = OneProjectAnalyzer(subjectDir + "MyWebMarket")
            results.addAll(myWebMarker.analyze())

            val wikidevFilters = OneProjectAnalyzer(subjectDir + "wikidev-filters")
            results.addAll(wikidevFilters.analyze())

            val myPlanner = OneProjectAnalyzer(subjectDir + "myplanner-data-src")
            results.addAll(myPlanner.analyze())

            for (tolerance in 1..3) {
                val tolResults = results.filter { it.tolerance == tolerance }

                val overallResult = Results(tolerance,
                        tolResults.flatMap { it.oracleCandidates }.toList(),
                        tolResults.flatMap { it.candidates }.toList()
                )
                log.info("overallResults:\n$overallResult")
            }
        } catch (e: Error) {
            val log = Logger.getLogger("error")
            log.severe("Error: $e")
        } catch (e: Exception) {
            val log = Logger.getLogger("exception")
            log.severe("Exception: $e")
        }
    }
}
