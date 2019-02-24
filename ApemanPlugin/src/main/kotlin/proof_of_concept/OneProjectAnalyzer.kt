package proof_of_concept

import apeman_core.Launcher
import apeman_core.methodsToScope
import apeman_core.pipes.CandidatesWithFeaturesAndProba
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.Logger
import kotlin.streams.toList

class OneProjectAnalyzer(private val dirOfProject: String) {
    private val log = Logger.getLogger("OneProjectAnalyzer")
    private var project: Project? = null
    private val apemanCandidates = arrayListOf<CandidatesWithFeaturesAndProba>()
    private val oracleEntries = arrayListOf<OracleEntry>()

    fun analyze(): List<Results> {
        log.info("analyze project")
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

        log.info("open project")

        project = ProjectManager.getInstance().loadAndOpenProject(dirOfProject)!!
    }

    private fun launchApemanOnNeededMethods() {
        log.info("create scope")
        val methods = oracleEntries.map { it.method!! }.distinct()

        log.info("launch apeman")
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

            val precision = truePositives.toDouble() / apemanSet.size
            val recall = truePositives.toDouble() / oracleSet.size
            val results = Results(tolerance, apemanSet.size, oracleSet.size, precision, recall)

            log.info("oracle = ${oracleSet.size},\n" +
                    "apeman = ${apemanSet.size},\n" +
                    "precision = $precision,\n" +
                    "recall = $recall,\n" +
                    "f-measure = ${results.fMeasure}"
            )
            listOfResults.add(results)
        }
        return listOfResults
    }

    private fun calculateTruePositivesForTolerance(tolerance: Int): Int {
        var truePositives = 0

        for (oracleCand in oracleEntries) {
            val candSameMethod = apemanCandidates.filter { (cand, _, _) ->
                cand.sourceMethod == oracleCand.method
            }
            val oracleLines = oracleCand.candidate.toString()

            val isThereSameCand = candSameMethod.any { (cand, _, _) ->
                cand.toString()
                        .split("\n")
                        .filterNot { line -> oracleLines.contains(line) }
                        .count() <= 2 * tolerance
            }
            if (isThereSameCand)
                truePositives++
        }
        return truePositives
    }
}
