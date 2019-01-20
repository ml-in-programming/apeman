package apeman_core.candidates_generation

import CandidatesOfMethod
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

class CandidatesOfScope(val project: Project, val analysisScope: AnalysisScope) {

    var candidates = ArrayList<Candidate>()

    init {
        generateCandidates()
    }

    fun generateCandidates() {
        if (analysisScope.fileCount == 0)
            return

        analysisScope.accept(object : JavaRecursiveElementVisitor() {
            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)
                candidates.addAll(CandidatesOfMethod(method).candidates)
            }
        })
    }
}