package apeman_core.utils

import apeman_core.base_entities.BlockOfMethod
import com.intellij.psi.*

import java.lang.reflect.Array
import java.util.HashSet

object BlocksUtils {

    private var ourCount = 0

    private var ourStatementsCount = 0

    fun <T> getElementsOfBlock(
            block: BlockOfMethod, aClassElement: Class<T>): Set<T> {
        val result = HashSet<T>()

        for (i in 0 until block.statementsCount) {
            block[i].accept(
                    object : JavaRecursiveElementVisitor() {
                        override fun visitElement(element: PsiElement?) {
                            super.visitElement(element)
                            if (aClassElement.isAssignableFrom(element!!.javaClass)) {
                                result.add(aClassElement.cast(element))
                            }
                        }
                    }
            )
        }
        return result
    }

    fun <T> getCountOfElementFromBlock(block: BlockOfMethod, ourElement: T): Int {
        ourCount = 0

        for (i in 0 until block.statementsCount) {
            block[i].accept(object : JavaRecursiveElementVisitor() {
                override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
                    super.visitReferenceElement(reference)

                    if (reference.isReferenceTo(ourElement as PsiElement)) {
                        ourCount++
                    }
                }

                override fun visitElement(element: PsiElement?) {
                    super.visitElement(element)
                    if (ourElement === element) {
                        ourCount++
                    }
                }
            })
        }
        return ourCount
    }

    fun <T> getFreqOfElementFromBlock(block: BlockOfMethod, element: T): Double {
        val count = getCountOfElementFromBlock(block, element)
        return count.toDouble() / getNumStatementsRecursively(block)
    }

    fun getNumStatementsRecursively(block: BlockOfMethod): Int {
        ourStatementsCount = 0
        for (i in 0 until block.statementsCount) {
            block[i].accept(object : JavaRecursiveElementVisitor() {


                override fun visitStatement(statement: PsiStatement) {
                    super.visitStatement(statement)
                    if (statement.parent is PsiCodeBlock) {
                        ourStatementsCount++
                    }
                }
            })
        }
        return ourStatementsCount
    }

    fun getBlockFromMethod(method: PsiMethod): BlockOfMethod {
        val statements = method.body!!.statements
        return BlockOfMethod(statements)
    }
}
