package apeman_core.candidates_generation

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.utils.CandidateUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.parentsOfType

class CustomCandidates(
        private val selections: List<Pair<TextRange, PsiFile>>
) {
    val candidates = arrayListOf<ExtractionCandidate>()

    fun getCandidates(): List<ExtractionCandidate> {
        selections.forEach { (range, file) ->
            val topMethod = file
                    .findElementAt(range.endOffset)!!
                    .parentsOfType<PsiMethod>()
                    .last()
            val candidate = CandidateUtils.fromTextRange(range, topMethod)
            if (candidate != null)
                candidates.add(candidate)
        }
        return candidates
    }
}