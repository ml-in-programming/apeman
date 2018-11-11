import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore

class CandidatesOfProject(val project: Project) {

    val analysisScope = AnalysisScope(project)
    var candidates = ArrayList<Candidate>()
    val files: Collection<PsiFile>

    init {
        val fileNames = FilenameIndex.getAllFilenames(project)
        files = fileNames.flatMap { FilenameIndex.getFilesByName(
                project, it, analysisScope.toSearchScope() as GlobalSearchScope
        ).toList() }

        generateCandidates()
    }

    fun generateCandidates() {

        for (file in files) {
            file.accept(object : JavaRecursiveElementVisitor() {
                override fun visitMethod(method: PsiMethod) {
                    super.visitMethod(method)
                    candidates.addAll(CandidatesOfMethod(method).candidates)
                }
            })
        }
    }
}