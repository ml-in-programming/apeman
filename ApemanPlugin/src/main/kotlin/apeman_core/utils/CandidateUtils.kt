package apeman_core.utils

import apeman_core.pipes.CandidateWithFeatures
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement

object CandidateUtils {

    fun getCandidatesOfMethod(method: PsiMethod, allCandidates: List<CandidateWithFeatures>)
            = allCandidates
            .filter { it.candidate.sourceMethod == method }
            .toList()

    fun checkStartOfCandidates(
            statement: PsiStatement,
            candidates: List<CandidateWithFeatures>
    ) {
        for (candidate in candidates) {
            if (candidate.candidate.start == statement)
                candidate.candidate.isInCandidate = true
        }
    }

    fun checkEndOfCandidates(
            statement: PsiStatement,
            candidates: List<CandidateWithFeatures>
    ) {
        for (candidate in candidates) {
            if (candidate.candidate.end == statement)
                candidate.candidate.isInCandidate = false
        }
    }
}
