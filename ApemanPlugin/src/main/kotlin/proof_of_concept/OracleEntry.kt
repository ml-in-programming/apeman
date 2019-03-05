package proof_of_concept

import apeman_core.base_entities.ExtractionCandidate
import com.intellij.psi.PsiMethod

data class OracleEntry(
        val methodName: String,
        val startOffsets: Int,
        val lengthOffsets: Int,
        var method: PsiMethod,
        var candidate: ExtractionCandidate
)
