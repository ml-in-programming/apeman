package proof_of_concept

import com.intellij.openapi.application.ApplicationStarter
import setupLogs
import java.util.logging.Logger

class ProofOfConceptLauncher : ApplicationStarter {

    val log = Logger.getGlobal()
    override fun getCommandName() = "proof-launcher"

    override fun premain(args: Array<out String>?) {}

    override fun main(args: Array<String>) {
        setupLogs(log)

        val subjectDir = "/home/snyss/Prog/mm/diploma/main/apeman/GemsDataset/subjects/"
        val allResults = arrayListOf<Results>()
        val longResults = arrayListOf<Results>()

        try {
            listOf("junit3.8", "JHotDraw5.2", "MyWebMarket", "wikidev-filters", "myplanner-data-src").forEach {
                val (shortResultsTemp, longResultsTemp) = OneProjectAnalyzer(
                        subjectDir + it
                ).analyze()
                allResults.addAll(shortResultsTemp)
                longResults.addAll(longResultsTemp)
            }
            log.info("all results")
            analyzeResults(allResults)

            log.info("long results")
            analyzeResults(longResults)

        } catch (e: Error) {
            val log = Logger.getLogger("error")
            log.severe("Error: $e")
        } catch (e: Exception) {
            val log = Logger.getLogger("exception")
            log.severe("Exception: $e")
        }
    }

    fun analyzeResults(results: List<Results>) {
        for (tolerance in 1..3) {
            val tolResults = results.filter { it.tolerance == tolerance }
            val overallResult = Results(tolerance,
                    tolResults.flatMap { it.oracleCandidates }.toList(),
                    tolResults.flatMap { it.candidates }.toList()
            )
            log.info("overallResults:\n$overallResult")
        }
        log.info("\n\n")
    }
}
