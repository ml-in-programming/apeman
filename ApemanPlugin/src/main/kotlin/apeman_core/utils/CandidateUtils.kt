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

    fun getCandidatesOfMethod(method: PsiMethod, allCandidates: List<ExtractionCandidate>
    ): List<ExtractionCandidate> {
        return allCandidates.filter { it.sourceMethod == method }
    }

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

    fun fromTextRange(range: TextRange, currentMethod: PsiMethod): ExtractionCandidate? {

        var elem = currentMethod.containingFile.findElementAt(range.endOffset - 1)!!
        while (elem !is PsiFile && elem !is PsiCodeBlock) {
            elem = elem.parent
        }
        assert(elem is PsiCodeBlock)

        val candStatements = (elem as PsiCodeBlock).statements
                .filter { statement -> range.contains(statement.textRange) }

        return if (candStatements.count() > 0)
            ExtractionCandidate(candStatements.toTypedArray(), currentMethod)
        else null
    }

    public fun getSourceCandidate(method: PsiMethod, candidates: List<ExtractionCandidate>) = candidates
            .first { it.isSourceCandidate && it.sourceMethod === method }
}
