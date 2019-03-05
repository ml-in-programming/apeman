package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.BlockOfMethod
import apeman_core.base_entities.FeatureType
import apeman_core.pipes.CandidateWithFeatures
import apeman_core.utils.BlocksUtils
import apeman_core.utils.TypeUtils
import com.intellij.psi.*

import java.lang.reflect.InvocationTargetException
import java.util.ArrayList
import java.util.HashSet

class TypeAccessCouplingCohesionCandidateCalculator(
        candidates: ArrayList<CandidateWithFeatures>,
        neededFeature: FeatureType,
        isCouplingMethod: Boolean,
        isFirstPlace: Boolean) : AbstractCouplingCohesionCandidateCalculator<PsiType>(candidates, neededFeature, isCouplingMethod, isFirstPlace, PsiType::class.java) {

    override fun createVisitor() = Visitor()

    inner class Visitor : AbstractCouplingCohesionCandidateCalculator<PsiType>.CandidateVisitor()

    override fun getElementsFromBlock(block: BlockOfMethod): Set<PsiType> {
        val result = HashSet<PsiType>()

        for (i in 0 until block.statementsCount) {
            block[i].accept(object : JavaRecursiveElementVisitor() {
                override fun visitElement(element: PsiElement?) {
                    super.visitElement(element)
                    TypeUtils.tryAddTypeOfElementTo(result, element!!)
                }
            })
        }
        return result
    }

    override fun getCountOfElementFromBlock(block: BlockOfMethod, elem: PsiType?): Int {
        ourCount = 0

        for (i in 0 until block.statementsCount) {
            block[i].accept(object : JavaRecursiveElementVisitor() {

                override fun visitElement(element: PsiElement?) {
                    super.visitElement(element)

                    val gettingTypeMethod = TypeUtils.tryGetGetTypeMethod(element!!) ?: return

                    try {
                        if (gettingTypeMethod.invoke(element) === elem)
                            ourCount++
                    } catch (ignored: IllegalAccessException) {
                    } catch (ignored: InvocationTargetException) {
                    }

                }

                override fun visitMethod(method: PsiMethod) {
                    super.visitMethod(method)

                    if (elem === method.returnType)
                        ourCount++
                    ourCount += method.parameterList.parameters
                            .map { it.type }
                            .filter { t -> t === elem }
                            .count()
                }
            })
        }
        return ourCount
    }

    override fun getFreqOfElementFromBlock(block: BlockOfMethod, elem: PsiType): Double {
        val count = getCountOfElementFromBlock(block, elem)
        return count.toDouble() / BlocksUtils.getNumStatementsRecursively(block)
    }

    companion object {
        private var ourCount = 0
    }
}
