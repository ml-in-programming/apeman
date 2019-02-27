package proof_of_concept

import com.intellij.psi.PsiMethod
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

data class OracleEntry(
        val methodName: String,
        val startOffsets: Int,
        val lengthOffsets: Int,
        var method: PsiMethod,
        var candidate: ExtractionCandidate
)
