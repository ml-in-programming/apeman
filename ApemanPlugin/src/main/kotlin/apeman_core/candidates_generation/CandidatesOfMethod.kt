package apeman_core.candidates_generation

import apeman_core.base_entities.ExtractionCandidate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.psi.*
import com.intellij.refactoring.extractMethod.ExtractMethodHandler
import java.util.*
import kotlin.collections.ArrayList

class CandidatesOfMethod(private val sourceMethod: PsiMethod) {

    val candidates = ArrayList<ExtractionCandidate>()

    init {
        generateCandidates()
    }

    private fun generateCandidates() {
        sourceMethod.accept(object : JavaRecursiveElementVisitor() {
            override fun visitCodeBlock(block: PsiCodeBlock) {
                super.visitCodeBlock(block)
                generateCandidatesOfOneBlock(block)
            }
        })
    }

    private fun generateCandidatesOfOneBlock(block: PsiCodeBlock) {
        val n = block.statementCount
        val statements = block.statements
        var uniqueId = 0

        for (i in 0 until n) {
            for (j in i until n) {

                val candidateBlock = ExtractionCandidate(
                        Arrays.copyOfRange(statements, i, j + 1),
                        sourceMethod,
                        uniqueId
                )
                if (isValid(candidateBlock)) {
                    candidates.add(candidateBlock)
                    uniqueId++
                }
            }
        }
    }

    // get editor, select candidateBlock and check if we can extract it
    private fun isValid(candidateBlock: ExtractionCandidate): Boolean {

        val editor = getEditor()

        editor.selectionModel.setSelection(
                candidateBlock.start.textOffset,
                candidateBlock.end.textRange.endOffset
        )

        return ExtractMethodHandler().isAvailableForQuickList(
                editor,
                sourceMethod.containingFile,
                DataContext.EMPTY_CONTEXT
        )
    }

    private fun getEditor(): Editor {
        val document = PsiDocumentManager.getInstance(sourceMethod.project)
                .getDocument(sourceMethod.containingFile)!!

        return EditorFactory.getInstance().createEditor(document)!!
    }
}