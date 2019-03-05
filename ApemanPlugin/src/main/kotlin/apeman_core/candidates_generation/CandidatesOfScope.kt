package apeman_core.candidates_generation

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.utils.scopeToTopMethods
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod
import java.util.logging.Logger


val log = Logger.getLogger("CandidatesOfScope")!!


public class CandidatesOfScope(
        private val project: Project,
        private val analysisMethods: List<PsiMethod>
) {

    private var candidates = ArrayList<ExtractionCandidate>()

    init {
        generateCandidates()
    }

    fun getCandidates(): List<ExtractionCandidate> {
        return candidates.toList()
    }

    private fun generateCandidates() {
        if (analysisMethods.isEmpty())
            return

        analysisMethods.forEach {
            it.accept(object : JavaRecursiveElementVisitor() {

                private var nestingDepth = 0
                override fun visitMethod(method: PsiMethod) {
                    nestingDepth++
                    super.visitMethod(method)
                    if (nestingDepth == 1)
                        candidates.addAll(CandidatesOfMethod(method).candidates)

                    nestingDepth--
                }
            })
        }
    }
}

// factory for candidates (easier for me than define several constructors)
public fun CandidatesOfScope(project: Project, analysisScope: AnalysisScope): CandidatesOfScope {
    log.info(analysisScope.fileCount.toString())

    val methods = scopeToTopMethods(analysisScope)
    return CandidatesOfScope(project, methods)
}
