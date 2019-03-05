package apeman_core.utils

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement

object CandidateUtils {

    fun getCandidatesOfMethod(method: PsiMethod, allCandidates: List<ExtractionCandidate>)
            = allCandidates
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
}
