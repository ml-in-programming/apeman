package proof_of_concept

import apeman_core.Launcher
import apeman_core.base_entities.CandidatesWithFeaturesAndProba
import apeman_core.utils.BlocksUtils
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.*
import kotlin.streams.toList

class OneProjectAnalyzer(private val dirOfProject: String) {
    private val log = Logger.getGlobal()
    private var project: Project? = null
    private val apemanCandidates = arrayListOf<CandidatesWithFeaturesAndProba>()

    fun analyze(): Pair<List<Results>, List<Results>> {
        log.fine("analyze project")
        loadProject()
        val parser = OracleParser(dirOfProject, project!!)
        val oracleEntries = parser.parseOracle()

        val longEntries = oracleEntries.filter {
            BlocksUtils.getNumStatementsRecursively(it.candidate.block) >= 30
        }

        val allCandidates = launchApemanOnNeededMethods(oracleEntries)
        val longCandidates = launchApemanOnNeededMethods(longEntries)
        return calculateResults(oracleEntries, allCandidates) to calculateResults(longEntries, longCandidates)
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

    private fun launchApemanOnNeededMethods(entries: List<OracleEntry>): List<CandidatesWithFeaturesAndProba> {
        if (entries.isEmpty())
            return listOf()

        log.fine("create scope")
        val methods = entries.map { it.method }.distinct()

        log.fine("launch apeman")
        val launcher = Launcher(analysisMethods = methods, project = entries[0].method.project)
        return launcher.calculateCandidatesWithProba(EmptyProgressIndicator())
    }

    private fun calculateResults(entries: List<OracleEntry>, candidates: List<CandidatesWithFeaturesAndProba>): List<Results> {
//        assert(candidates.isNotEmpty())
//        assert(entries.isNotEmpty())

        val listOfResults = mutableListOf<Results>()

        for (tolerance in 1..3) {
            val results = Results(
                    tolerance,
                    entries.map { it.candidate },
                    candidates
            )
            log.info(results.toString())
            listOfResults.add(results)
        }
        return listOfResults
    }
}
