package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.BlockOfMethod
import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import apeman_core.utils.ClassUtils
import com.intellij.psi.*

import java.util.HashSet

class PackageAccessCouplingCohesionCandidateCalculator(
        candidates: List<ExtractionCandidate>,
        neededFeature: FeatureType,
        isCouplingMethod: Boolean,
        isFirstPlace: Boolean) : AbstractCouplingCohesionCandidateCalculator<PsiPackage>(candidates, neededFeature, isCouplingMethod, isFirstPlace, PsiPackage::class.java) {

    override fun createVisitor(): CandidateVisitor = Visitor()

    inner class Visitor : AbstractCouplingCohesionCandidateCalculator<PsiPackage>.CandidateVisitor()

    override fun getElementsFromBlock(block: BlockOfMethod): Set<PsiPackage> {
        assert(block.statementsCount > 0)
        val firstPackage = ClassUtils.findPackage(block.firstStatement)
        val result = hashSetOf<PsiPackage>()
        if (firstPackage != null)
            result.add(firstPackage)

        for (i in 0 until block.statementsCount) {
            block[i].accept(object : JavaRecursiveElementVisitor() {
                override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
                    super.visitReferenceElement(reference)

                    val elem = reference.resolve() ?: return
                    val psiPackage = ClassUtils.findPackage(elem) ?: return

                    result.add(psiPackage)
                }

                override fun visitPackage(aPackage: PsiPackage) {
                    super.visitPackage(aPackage)
                    result.add(aPackage)
                }
            })
        }
        return result
    }

    override fun getCountOfElementFromBlock(block: BlockOfMethod, elem: PsiPackage?): Int {
        ourCount = 0

        for (i in 0 until block.statementsCount) {
            block[i].accept(object : JavaRecursiveElementVisitor() {
                override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
                    super.visitReferenceElement(reference)

                    val resolved = reference.resolve() ?: return
                    val resolvedPackage = ClassUtils.findPackage(resolved) ?: return

                    if (elem === resolvedPackage) {
                        ourCount++
                    }
                }
            })
        }
        return ourCount
    }

//    override fun getFreqOfElementFromBlock(block: BlockOfMethod, elem: PsiPackage): Double {
//        val count = getCountOfElementFromBlock(block, elem)
//        return count.toDouble() / BlocksUtils.getNumStatementsRecursively(block)
//    }

    companion object {

        private var ourCount = 0
    }
}
