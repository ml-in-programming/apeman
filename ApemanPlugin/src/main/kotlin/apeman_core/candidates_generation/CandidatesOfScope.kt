package apeman_core.candidates_generation

import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

public class CandidatesOfScope(
        private val project: Project,
        private val analysisScope: AnalysisScope
) {

    private var candidates = ArrayList<ExtractionCandidate>()

    init {
        generateCandidates()
    }

    public fun getCandidates(): List<ExtractionCandidate> {
        return candidates.toList()
    }

    private fun generateCandidates() {
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
