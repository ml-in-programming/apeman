package gui

import apeman_core.Launcher
import com.intellij.analysis.AnalysisScope
import com.intellij.analysis.BaseAnalysisAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.parents
import setupLogs
import java.time.LocalDateTime
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

private val log = Logger.getGlobal().also { setupLogs(it) }

class AnalysisScopeLauncher : BaseAnalysisAction("check1", "check2") {

    override fun analyze(project: Project, scope: AnalysisScope) {
        val launcher = Launcher(scope, project = project)
        val candidates = launcher.calculateCandidatesWithProbaAsync(project)
        CandidatesTable(candidates, project)
    }
}

class AnalysisMethodLauncher : AnAction("check only 1 method") {
    override fun actionPerformed(e: AnActionEvent) {
        val caret = e.getData(CommonDataKeys.CARET)!!
        val file = e.getData(CommonDataKeys.PSI_FILE)!!

        val elem = file.findElementAt(caret.offset)
        val parents = elem!!.parents().toList()
        val method = parents.lastOrNull { it is PsiMethod }
        if (method == null) {
            log.warning("caret is not in method!")
            return
        }

        val launcher = Launcher(analysisMethods = listOf(method as PsiMethod), project = method.project)
        val candidates = launcher.calculateCandidatesWithProbaAsync(method.project)
        CandidatesTable(candidates, e.getData(CommonDataKeys.PROJECT)!!)
//        createJBTable(candidates, e.getData(CommonDataKeys.EDITOR)!!, e.getData(CommonDataKeys.PROJECT)!!)
//        showInfoDialog(candidates)
    }
}

class AnalysisSelectionLauncher : AnAction("check only 1 selection of candidate") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)!!
        val textRange = TextRange(
                editor.selectionModel.selectionStart,
                editor.selectionModel.selectionEnd)
        val file = e.getData(CommonDataKeys.PSI_FILE)!!
        val launcher = Launcher(analysisCandidates = listOf(textRange to file), project = file.project)
        val candidates = launcher.calculateCandidatesWithProbaAsync(file.project)
        CandidatesTable(candidates, e.getData(CommonDataKeys.PROJECT)!!)

//   createJBTable(candidates, e.getData(CommonDataKeys.EDITOR)!!, e.getData(CommonDataKeys.PROJECT)!!)
//        showInfoDialog(candidates)
    }
}
