package proof_of_concept

import apeman_core.Launcher
import apeman_core.base_entities.CandidatesWithFeaturesAndProba
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
        val launcher = Launcher(analysisMethods = methods)
        apemanCandidates.addAll(launcher.calculateCandidatesWithProba(EmptyProgressIndicator()))
    }

    private fun calculateResults(): List<Results> {
        assert(apemanCandidates.isNotEmpty())
        assert(oracleEntries.isNotEmpty())

        val listOfResults = mutableListOf<Results>()

        for (tolerance in 1..3) {
            val results = Results(
                    tolerance,
                    oracleEntries.map { it.candidate },
                    apemanCandidates
            )
            log.info(results.toString())
            listOfResults.add(results)
        }
        return listOfResults
    }
}
