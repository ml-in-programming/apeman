import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.file.impl.FileManager
import com.intellij.psi.impl.file.impl.FileManagerImpl
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class CandidatesOfScope(val project: Project, val analysisScope: AnalysisScope) {

    var candidates = ArrayList<ExtractionCandidate>()

    init {
        generateCandidates()
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