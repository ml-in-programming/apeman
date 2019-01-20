package apeman_core.candidates_generation

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.psi.*
import com.intellij.refactoring.extractMethod.ExtractMethodHandler
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate
import java.util.*
import kotlin.collections.ArrayList

class CandidatesOfMethod(val sourceMethod: PsiMethod) {

    var candidates = ArrayList<Candidate>()

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

                val candidateBlock = ExtractionCandidate(Arrays.copyOfRange(statements, i, j + 1), sourceMethod)
                if (isValid(candidateBlock))
                    candidates.add(Candidate(candidateBlock))
            }
        }
    }

    // get editor, select candidateBlock and check if we can extract it
    fun isValid(candidateBlock: ExtractionCandidate): Boolean {

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

    fun getTopKCandidates(k: Int, candToProba: Collection<Pair<ExtractionCandidate, Double>>)
            : ArrayList<Pair<ExtractionCandidate, Double>>  {

        return ArrayList(candToProba.toMap()
                .filterKeys { cand -> this.candidates.map { it.candidate }.contains(cand) }
                .toList()
                .sortedBy { candAndProba -> -candAndProba.second }
                .take(k))
    }
}