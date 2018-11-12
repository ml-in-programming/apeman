import com.intellij.openapi.project.Project
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

class CandidatesOfProject(val project: Project) {

    val globalSearchScope = GlobalSearchScope.projectScope(project)
    var candidates = ArrayList<ExtractionCandidate>()
    val files: Collection<PsiFile>

    init {
        val fileNames = FilenameIndex.getAllFilenames(project)
        files = fileNames.flatMap {
            FilenameIndex.getFilesByName(project, it, globalSearchScope).toList()
        }

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