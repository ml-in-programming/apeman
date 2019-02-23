package proof_of_concept

import com.intellij.psi.PsiMethod
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

data class OracleEntry(val methodName: String, val startOffsets: ArrayList<Int>, val lengthOffsets: ArrayList<Int>) {
    var method: PsiMethod? = null
    var candidate: ExtractionCandidate? = null
}
