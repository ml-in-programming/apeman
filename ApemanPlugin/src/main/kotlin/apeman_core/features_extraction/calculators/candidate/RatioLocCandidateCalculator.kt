package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.features_extraction.calculators.BaseMetricsCalculator
import apeman_core.utils.BlocksUtils
import apeman_core.utils.CandidateUtils
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiMethod

class RatioLocCandidateCalculator(candidates: List<ExtractionCandidate>
) : BaseMetricsCalculator(candidates, FeatureType.LOC_RATIO) {

    override fun createVisitor(methodCandidates: List<ExtractionCandidate>): JavaRecursiveElementVisitor {
        return object : JavaRecursiveElementVisitor() {

            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)
                if (method.body == null)
                // abstract method or interface
                    return
                val blockOfMethod = BlocksUtils.getBlockFromMethod(method)
                val candidatesOfMethod = CandidateUtils.getCandidatesOfMethod(method, candidates)
                val numStatementsMethod = BlocksUtils.getNumStatementsRecursively(blockOfMethod)

                for (cand in candidatesOfMethod) {
                    val candStatements = BlocksUtils.getNumStatementsRecursively(cand.block)
                    results.set(cand, firstFeature, candStatements / numStatementsMethod.toDouble())
                }
            }
        }
    }
}
