package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.NotOptimizedCalculator
import apeman_core.utils.BlocksUtils
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

class LocCandidate(candidates: List<ExtractionCandidate>
) : NotOptimizedCalculator(candidates, FeatureType.NUM_LOC) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>): JavaRecursiveElementVisitor {
        return object : JavaRecursiveElementVisitor() {

            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)

                if (methodCandidates.count() == 0)
                    return
                if (method !== methodCandidates[0].sourceMethod)
                    return

                for (cand in methodCandidates) {
                    results.set(cand, firstFeature,
                            BlocksUtils.getNumStatementsRecursively(cand.block).toDouble())
                }
            }
        }
    }
}
