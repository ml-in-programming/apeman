import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.psi.*
import com.intellij.refactoring.extractMethod.ExtractMethodHandler

class CandidatesOfMethod(val sourceMethod: PsiMethod) {

    var candidates = ArrayList<Candidate>()

    init {
        fillCandidates()
        candidates.forEach{println(it)}
    }

    fun fillCandidates() {
        sourceMethod.accept(object : PsiRecursiveElementVisitor() {

            override fun visitElement(element: PsiElement?) {
                super.visitElement(element)

                if (element is PsiJavaToken && element.tokenType == JavaTokenType.LBRACE) {
                    val codeBlock = element.parent

                    if (codeBlock as? PsiCodeBlock == null)
                        return

                    generateCandidatesOfOneBlock(codeBlock)
                }
            }
        })
    }

    fun generateCandidatesOfOneBlock(block: PsiCodeBlock) {
        val n = block.statementCount

        for (i in 0 until n) {
            for (j in (i + 1) until n) {

                val candidate = Candidate(block.statements[i], block.statements[j], sourceMethod)
                if (isValid(candidate))
                    candidates.add(candidate)
            }
        }
    }

    // get editor, select candidate and check if we can extract it
    fun isValid(candidate: Candidate): Boolean {

        val document = PsiDocumentManager.getInstance(sourceMethod.project)
                .getDocument(sourceMethod.containingFile) ?: return false

        val editor = EditorFactory.getInstance().createEditor(document) ?: return false

        editor.selectionModel.setSelection(
                candidate.start.textOffset,
                candidate.end.textOffset
        )

        return ExtractMethodHandler().isAvailableForQuickList(
                editor,
                sourceMethod.containingFile,
                DataContext.EMPTY_CONTEXT
        )
    }
}