package apeman_core.features_extraction.calculators

import com.intellij.psi.PsiMethod

abstract class SuperBaseCalculator {
    abstract val results: Results
    abstract fun calculateMethod(method: PsiMethod)
}