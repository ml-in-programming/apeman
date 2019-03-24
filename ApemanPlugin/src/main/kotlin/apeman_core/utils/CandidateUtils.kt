package apeman_core.utils

import apeman_core.base_entities.ExtractionCandidate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.extractMethod.ExtractMethodHandler

object CandidateUtils {

    fun getCandidatesOfMethod(method: PsiMethod, allCandidates: List<ExtractionCandidate>) = allCandidates
            .filter { it.sourceMethod == method }
            .toList()

    fun checkStartOfCandidates(
            statement: PsiStatement,
            candidates: List<ExtractionCandidate>
    ) {
        for (candidate in candidates) {
            if (candidate.start == statement)
                candidate.isInCandidate = true
        }
    }

    fun checkEndOfCandidates(
            statement: PsiStatement,
            candidates: List<ExtractionCandidate>
    ) {
        for (candidate in candidates) {
            if (candidate.end == statement)
                candidate.isInCandidate = false
        }
    }

    fun fromTextRange(range: TextRange, currentMethod: PsiMethod): ExtractionCandidate {

        var elem = currentMethod.containingFile.findElementAt(range.endOffset - 1)!!
        while (elem !is PsiFile && elem !is PsiCodeBlock) {
            elem = elem.parent
        }
        assert(elem is PsiCodeBlock)

        val candStatements = (elem as PsiCodeBlock).statements
                .filter { statement -> range.contains(statement.textRange) }

        assert(candStatements.count() > 0)
        return ExtractionCandidate(candStatements.toTypedArray(), currentMethod)
    }

    // get editor, select candidate and check if we can extract it
    fun isValid(candidate: ExtractionCandidate): Boolean {

        val editor = getEditor(candidate)

        editor.selectionModel.setSelection(
                candidate.start.textOffset,
                candidate.end.textRange.endOffset
        )

        return ExtractMethodHandler().isAvailableForQuickList(
                editor,
                candidate.sourceMethod.containingFile,
                DataContext.EMPTY_CONTEXT
        )
    }

    private fun getEditor(candidate: ExtractionCandidate): Editor {
        val document = PsiDocumentManager.getInstance(candidate.sourceMethod.project)
                .getDocument(candidate.sourceMethod.containingFile)!!

        return EditorFactory.getInstance().createEditor(document)!!
    }

    public fun getSourceCandidate(method: PsiMethod, candidates: List<ExtractionCandidate>) = candidates
            .first { it.isSourceCandidate && it.sourceMethod === method }
}
