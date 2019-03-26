package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import com.intellij.psi.PsiMethod

abstract class SuperBaseCalculator {
    abstract val results: Results
    abstract fun calculateMethod(method: PsiMethod, methodCandidates: List<ExtractionCandidate>)
}