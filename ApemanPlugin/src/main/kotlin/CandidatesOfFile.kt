import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiRecursiveElementVisitor

class CandidatesOfFile(val file: PsiFile) {

    var candidates = ArrayList<Candidate>()

    init {
        generateCandidatesForEachMethod()
    }

    fun generateCandidatesForEachMethod() {
        file.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement?) {
                super.visitElement(element)
                if (element is PsiMethod) {
                    candidates.addAll(CandidatesOfMethod(element).candidates)
                }
            }
        })
    }
}