package apeman_core.features_extraction.calculators.method

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.utils.CandidateUtils
import apeman_core.utils.MethodUtils
import apeman_core.utils.TypeUtils
import com.intellij.psi.*

import java.util.ArrayList

class NumTypedElementsMethodCalculator(candidates: List<ExtractionCandidate>
) : NumSimpleElementMethodCalculator(candidates, FeatureType.CON_TYPED_ELEMENTS) {

    private var methodNestingDepth = 0
    //    private var typeSet: MutableSet<PsiType>? = null

    override fun createVisitor(): NumSimpleElementMethodCalculator.Visitor {
        return Visitor()
    }

    private inner class Visitor : NumSimpleElementMethodCalculator.Visitor() {

        private var typeCollection = arrayListOf<PsiType>()

        override fun visitMethod(method: PsiMethod) {
//            if (methodNestingDepth == 0) {
//                typeSet = HashSet()
//            }
            TypeUtils.addTypesFromMethodTo(typeCollection, method)
            elementsCounter += typeCollection.count()
            typeCollection.clear()

//            methodNestingDepth++
            super.visitMethod(method)
//            methodNestingDepth--
//            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
//                val res = typeCollection!!.count().toDouble()
//                CandidateUtils
//                        .getCandidatesOfMethod(method, candidates)
//                        .forEach { results.set(it, firstFeature, res) }
//            }
        }

        override fun visitElement(element: PsiElement) {
            super.visitElement(element)
            TypeUtils.tryAddTypeOfElementTo(typeCollection, element)
            elementsCounter += typeCollection.count()
            typeCollection.clear()
        }
    }
}
