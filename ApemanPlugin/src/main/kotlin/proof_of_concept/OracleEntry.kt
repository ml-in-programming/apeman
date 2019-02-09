package proof_of_concept

import com.intellij.psi.PsiMethod
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

data class OracleEntry(val methodName: String, val offset: String) {
    var method: PsiMethod? = null
    var candidate: ExtractionCandidate? = null
}
