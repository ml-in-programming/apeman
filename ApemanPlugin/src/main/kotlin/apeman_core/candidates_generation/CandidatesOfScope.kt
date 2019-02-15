package apeman_core.candidates_generation

import apeman_core.scopeToTopMethods
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiLambdaExpression
import com.intellij.psi.PsiMethod
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate
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

// factory for candidates (easier than define several constructors)
public fun CandidatesOfScope(project: Project, analysisScope: AnalysisScope): CandidatesOfScope {
    log.info(analysisScope.fileCount.toString())

    val methods = scopeToTopMethods(analysisScope)
    return CandidatesOfScope(project, methods)
}
