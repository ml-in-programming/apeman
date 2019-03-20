package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.ClassUtils
import com.intellij.psi.*

import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet

class NumPackageAccessesCandidateCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_PACKAGE) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {
        var packages = listOf<PsiPackage>()

//        override fun updateCounter(i: Int) {
//            if (methodCandidates[i].isInCandidate) {
//                counts[i] += packages.count()
//            }
//        }

        override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
            super.visitReferenceElement(reference)

            if (!isInsideMethod) return
            val element = reference.resolve() ?: return
            ClassUtils.findPackage(element) ?: return

            updateCounters()
        }
    }
}
