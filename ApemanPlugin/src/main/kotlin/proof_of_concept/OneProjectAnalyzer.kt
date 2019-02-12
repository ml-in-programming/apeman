package proof_of_concept

import apeman_core.Launcher
import apeman_core.pipes.CandidatesWithFeaturesAndProba
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.application.impl.ApplicationImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.Logger
import kotlin.streams.toList

class OneProjectAnalyzer(private val dirOfProject: String) {
    private val log = Logger.getLogger("OneProjectAnalyzer")
    private var project: Project? = null
    private var scope: AnalysisScope? = null
    private val apemanCandidates = arrayListOf<CandidatesWithFeaturesAndProba>()
    private val oracleEntries = arrayListOf<OracleEntry>()

    fun analyze() {
        log.info("analyze project")
        loadProjectAndScope()
        parseOracleFile()
        //launchApeman()
    }

    private fun loadProjectAndScope() {

        val dirpath = Paths.get(dirOfProject)
        log.info("path: ${dirpath.toUri()}")

        assert(Files.isDirectory(dirpath))
        val listOfFiles = Files.list(dirpath).toList()
        assert(listOfFiles.joinToString(" ").contains("oracle.txt"))

        log.info("open project")

        project = ProjectManager.getInstance().loadAndOpenProject(dirOfProject)!!

        log.info("create scope")
        scope = AnalysisScope(project!!)
    }

    private fun parseOracleFile() {
        val parser = OracleParser(dirOfProject, project!!, scope!!)
        oracleEntries.addAll(parser.parseOracle())
    }

    private fun launchApeman() {
        val launcher = Launcher(project!!, scope!!)
        apemanCandidates.addAll(launcher.getCandidatesWithProba())
    }
}
