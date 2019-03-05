package proof_of_concept

import apeman_core.Launcher
import apeman_core.utils.methodsToScope
import apeman_core.pipes.CandidatesWithFeaturesAndProba
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.logging.*
import kotlin.streams.toList

class OneProjectAnalyzer(private val dirOfProject: String) {
    private val log = Logger.getGlobal()
    private var project: Project? = null
    private val apemanCandidates = arrayListOf<CandidatesWithFeaturesAndProba>()
    private val oracleEntries = arrayListOf<OracleEntry>()

    fun analyze(): List<Results> {
        log.fine("analyze project")
        loadProject()
        val parser = OracleParser(dirOfProject, project!!)
        oracleEntries.addAll(parser.parseOracle())

        launchApemanOnNeededMethods()
        return calculateResults()
    }

    private fun loadProject() {

        val dirpath = Paths.get(dirOfProject)
        log.info("path: ${dirpath.toUri()}")

        assert(Files.isDirectory(dirpath))
        val listOfFiles = Files.list(dirpath).toList()
        assert(listOfFiles.joinToString(" ").contains("oracle.txt"))

        log.fine("open project")

        project = ProjectManager.getInstance().loadAndOpenProject(dirOfProject)!!
    }

    private fun launchApemanOnNeededMethods() {
        log.fine("create scope")
        val methods = oracleEntries.map { it.method }.distinct()

        log.fine("launch apeman")
        val launcher = Launcher(project!!, analysisScope = methodsToScope(methods), analysisMethods = methods)
        apemanCandidates.addAll(launcher.getCandidatesWithProba())
    }

    private fun calculateResults(): List<Results> {
        assert(apemanCandidates.isNotEmpty())
        assert(oracleEntries.isNotEmpty())

        val listOfResults = mutableListOf<Results>()
        val apemanSet = apemanCandidates.toSet()
        val oracleSet = oracleEntries.toSet()

        for (tolerance in 1..3) {
            val truePositives = calculateTruePositivesForTolerance(tolerance)

            val precision = truePositives.count().toDouble() / apemanSet.size
            val recall = truePositives.count().toDouble() / oracleSet.size
            val results = Results(tolerance, apemanSet.size, oracleSet.size, precision, recall, oracleEntries.map { it.candidate }, truePositives)

            log.info("tolerance = $tolerance,\noracle = ${oracleSet.size},\n" +
                    "apeman = ${apemanSet.size},\n" +
                    "true positives = ${truePositives.count()},\n" +
                    "precision = $precision,\n" +
                    "recall = $recall,\n" +
                    "f-score = ${results.fMeasure}"
            )
            listOfResults.add(results)
        }
        return listOfResults
    }

    private fun calculateTruePositivesForTolerance(tolerance: Int): List<CandidatesWithFeaturesAndProba> {
        val truePositives = arrayListOf<CandidatesWithFeaturesAndProba>()

        for (oracleCand in oracleEntries) {
            val candSameMethod = apemanCandidates.filter { (cand, _, _) ->
                cand.sourceMethod == oracleCand.method
            }
            val oracleLines = oracleCand.candidate.toString().split("\n")

            val sameCand = candSameMethod.firstOrNull { (cand, _, _) ->
                val (same, notSame) = cand.toString()
                        .split("\n")
                        .partition { line -> oracleLines.contains(line) }
                val maxDiff = 2 * tolerance
                return@firstOrNull notSame.count() <= maxDiff && same.count() > oracleLines.count() - maxDiff

            }
            if (sameCand != null) {
                truePositives.add(sameCand)

            }
        }
        return truePositives
    }
}
