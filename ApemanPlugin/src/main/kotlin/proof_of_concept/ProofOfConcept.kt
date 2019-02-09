package proof_of_concept

import apeman_core.Launcher
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.util.io.isDirectory
import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.Logger
import kotlin.streams.toList

private val log = Logger.getLogger("proof_of_concept/ProofOfConcept.ktProofOfConcept.kt")

fun analyzeProject(dirname: String) {
    val project = loadProject(dirname)

    log.info("create scope")
    val scope = AnalysisScope(project)

    log.info("analyze project")
    val launcher = Launcher(project, scope)
    val candToProba = launcher.getCandidatesWithProba()
    candToProba
}

private fun loadProject(dirname: String): Project {

    val dirpath = Paths.get(dirname)
    log.info("path: ${dirpath.toUri()}")

    assert(dirpath.isDirectory())
    val listOfFiles = Files.list(dirpath).toList()
    assert(listOfFiles.joinToString(" ").contains("oracle.txt"))

    log.info("open project")
    val project = ProjectManager.getInstance().loadAndOpenProject(dirname)!!
    return project
}

