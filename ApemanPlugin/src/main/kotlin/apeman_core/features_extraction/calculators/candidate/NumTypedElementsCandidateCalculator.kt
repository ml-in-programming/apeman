package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.utils.TypeUtils
import com.intellij.psi.*

class NumTypedElementsCandidateCalculator(candidates: List<ExtractionCandidate>
) : AbstractNumCandidateCalculator(candidates, FeatureType.NUM_TYPED_ELEMENTS) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractNumCandidateCalculator.CandidateVisitor() {

        var usedTypes = arrayListOf<PsiType>()
//        internal var usedTypes: ArrayList<HashSet<PsiType>>? = null

//        override fun initCounters() {
//            if (usedTypes == null) {
//                usedTypes = ArrayList()
//            }
//            usedTypes!!.clear()
//            methodCandidates.forEach { usedTypes!!.add(HashSet()) }
//        }

//        override fun getCounterForCand(i: Int): Int {
//            return usedTypes!![i].size
//        }

//        override fun visitMethod(method: PsiMethod) {
//            super.visitMethod(method)
//            if (!isInsideMethod)
//                return
//
//            for (i in methodCandidates.indices) {
//                if (methodCandidates[i].isInCandidate) {
//                    TypeUtils.addTypesFromMethodTo(usedTypes!![i], method)
//                }
//            }
//        }

        override fun visitElement(element: PsiElement?) {
            super.visitElement(element)
            if (!isInsideMethod)
                return
            TypeUtils.tryAddTypeOfElementTo(usedTypes, element!!)
            updateCounters()

//            for (i in methodCandidates.indices) {
//                if (methodCandidates[i].isInCandidate) {
//                    TypeUtils.tryAddTypeOfElementTo(usedTypes!![i], element!!)
//                }
//            }
        }

        override fun updateCounters() {
            super.updateCounters()
            usedTypes.clear()
        }

        override fun updateCounter(i: Int) {
            if (methodCandidates[i].isInCandidate) {
                counts[i] += usedTypes.count()
            }
        }
    }
}
