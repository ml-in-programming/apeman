package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.BlockOfMethod
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.BlocksUtils
import apeman_core.utils.ClassUtils
import com.intellij.psi.*

import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet

class PackageAccessCouplingCohesionCandidateCalculator(
        candidates: ArrayList<CandidateWithFeatures>,
        neededFeature: FeatureType,
        isCouplingMethod: Boolean,
        isFirstPlace: Boolean) : AbstractCouplingCohesionCandidateCalculator<PsiPackage>(candidates, neededFeature, isCouplingMethod, isFirstPlace, PsiPackage::class.java) {

    override fun createVisitor(): CandidateVisitor = Visitor()

    inner class Visitor : AbstractCouplingCohesionCandidateCalculator<PsiPackage>.CandidateVisitor()

    override fun getElementsFromBlock(block: BlockOfMethod): Set<PsiPackage> {
        assert(block.statementsCount > 0)
        val result = HashSet(ClassUtils.calculatePackagesRecursive(block.firstStatement).toList())

        for (i in 0 until block.statementsCount) {
            block[i].accept(object : JavaRecursiveElementVisitor() {
                override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
                    super.visitReferenceElement(reference)
                    val elem = reference.resolve()
                    if (elem == null || elem.containingFile == null)
                        return

                    val packages = ClassUtils.calculatePackagesRecursive(elem)
                    result.addAll(Arrays.asList(*packages))
                }

                override fun visitPackage(aPackage: PsiPackage) {
                    super.visitPackage(aPackage)
                    result.add(aPackage)
                }
            })
        }
        return result
    }

    override fun getCountOfElementFromBlock(block: BlockOfMethod, psiPackage: PsiPackage?): Int {
        ourCount = 0

        for (i in 0 until block.statementsCount) {
            block[i].accept(object : JavaRecursiveElementVisitor() {
                override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
                    super.visitReferenceElement(reference)
                    val resolved = reference.resolve()
                    if (resolved == null || resolved.containingFile == null)
                        return

                    val packages = ClassUtils
                            .calculatePackagesRecursive(reference.resolve() ?: return)
                    for (pack in packages) {
                        if (pack === psiPackage)
                            ourCount++
                    }
                }

                override fun visitPackage(aPackage: PsiPackage) {
                    super.visitPackage(aPackage)
                    if (aPackage === psiPackage)
                        ourCount++
                }
            })
        }
        return ourCount
    }

    override fun getFreqOfElementFromBlock(block: BlockOfMethod, elem: PsiPackage): Double {
        val count = getCountOfElementFromBlock(block, elem)
        return count.toDouble() / BlocksUtils.getNumStatementsRecursively(block)
    }

    companion object {

        private var ourCount = 0
    }
}
