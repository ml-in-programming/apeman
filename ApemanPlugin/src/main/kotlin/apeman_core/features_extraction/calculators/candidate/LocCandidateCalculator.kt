package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.BlocksUtils
import apeman_core.utils.CandidateUtils
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

import java.util.ArrayList

class LocCandidateCalculator(candidates: List<ExtractionCandidate>
) : BaseMetricsCalculator(candidates, FeatureType.NUM_LOC) {

    override fun createVisitor(): JavaRecursiveElementVisitor {
        return object : JavaRecursiveElementVisitor() {

            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)
                val candidatesOfMethod = CandidateUtils.getCandidatesOfMethod(method, candidates)

                for (cand in candidatesOfMethod) {
                    results.set(cand, firstFeature,
                            BlocksUtils.getNumStatementsRecursively(cand.block).toDouble())
                }
            }
        }
    }
}
