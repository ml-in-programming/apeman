package proof_of_concept

import com.intellij.openapi.application.ApplicationStarter
import java.util.logging.Logger
import kotlin.system.exitProcess

class ProofOfConceptLauncher : ApplicationStarter {

    override fun getCommandName() = "proof-launcher"

    override fun premain(args: Array<out String>?) {}

    override fun main(args: Array<String>) {

//        val subjectDir = "/home/snyss/Prog/mm/diploma/main/apeman/GemsDataset/subjects/"
        val subjectDir = "/home/snyss/Prog/mm/diploma/gems_datasets/subjects/"

        try {
            val junit = OneProjectAnalyzer(subjectDir + "junit3.8")
            junit.analyze()

            val jHotDraw = OneProjectAnalyzer(subjectDir + "JHotDraw5.2")
            jHotDraw.analyze()

            val myWebMarker = OneProjectAnalyzer(subjectDir + "MyWebMarket")
            myWebMarker.analyze()

            val wikidevFilters = OneProjectAnalyzer(subjectDir + "wikidev-filters")
            wikidevFilters.analyze()

            val myPlanner = OneProjectAnalyzer(subjectDir + "myplanner-data-src")
            myPlanner.analyze()

        } catch (e: Error) {
            val log = Logger.getLogger("null pointer exception")
            log.info("Lol: " + e.stackTrace.toString())
        } catch (e: Exception) {
            val log = Logger.getLogger("null pointer exception")
            log.info("Lol: " + e.stackTrace.toString())
        }
    }
}
