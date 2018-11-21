import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.EditorFactory
import com.intellij.psi.*
import com.intellij.refactoring.extractMethod.ExtractMethodHandler
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate
import java.util.*

class CandidatesOfMethod(val sourceMethod: PsiMethod) {

    var candidates = ArrayList<ExtractionCandidate>()

    init {
        fillCandidates()
    }

    fun fillCandidates() {
        sourceMethod.accept(object : JavaRecursiveElementVisitor() {

            override fun visitCodeBlock(block: PsiCodeBlock) {
                super.visitCodeBlock(block)
                generateCandidatesOfOneBlock(block)
            }
        })
    }

    fun generateCandidatesOfOneBlock(block: PsiCodeBlock) {
        val n = block.statementCount
        val statements = block.statements

        for (i in 0 until n) {
            for (j in i until n) {

                val candidate = ExtractionCandidate(Arrays.copyOfRange(statements, i, j + 1), sourceMethod)
                if (isValid(candidate))
                    candidates.add(candidate)
            }
        }
    }

    // get editor, select candidate and check if we can extract it
    fun isValid(candidate: ExtractionCandidate): Boolean {

        val document = PsiDocumentManager.getInstance(sourceMethod.project)
                .getDocument(sourceMethod.containingFile) ?: return false

        val editor = EditorFactory.getInstance().createEditor(document) ?: return false

        editor.selectionModel.setSelection(
                candidate.start.textOffset,
                candidate.end.textRange.endOffset
        )

        return ExtractMethodHandler().isAvailableForQuickList(
                editor,
                sourceMethod.containingFile,
                DataContext.EMPTY_CONTEXT
        )
    }
}