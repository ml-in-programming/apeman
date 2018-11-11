import com.intellij.psi.*

class CandidatesOfFile(val file: PsiFile) {

    var candidates = ArrayList<Candidate>()

    init {
        generateCandidatesForEachMethod()
    }

    fun generateCandidatesForEachMethod() {
        file.accept(object : JavaRecursiveElementVisitor() {

            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)
                candidates.addAll(CandidatesOfMethod(method).candidates)
            }
        })
    }
}