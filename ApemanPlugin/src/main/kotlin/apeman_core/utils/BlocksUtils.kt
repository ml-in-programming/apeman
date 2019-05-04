package apeman_core.utils

import apeman_core.base_entities.BlockOfMethod
import com.intellij.psi.*

import java.lang.reflect.Array
import java.util.HashSet

object BlocksUtils {

    private var ourCount = 0

    private var ourStatementsCount = 0

    fun <T> getElementsOfBlock(block: BlockOfMethod, aClassElement: Class<T>): Set<T> {
        val result = HashSet<T>()

        for (i in 0 until block.statementsCount) {
            block[i].accept(
                    object : JavaRecursiveElementVisitor() {
                        override fun visitElement(element: PsiElement?) {
                            super.visitElement(element)
                            if (aClassElement.isAssignableFrom(element!!.javaClass)) {
                                result.add(aClassElement.cast(element))

                            } else if (element is PsiReference) {
                                val resolved = element.resolve() ?: return
                                if (aClassElement.isAssignableFrom(resolved.javaClass)) {
                                    result.add(aClassElement.cast(resolved))
                                }
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

    fun getStatementsRecursively(block: BlockOfMethod): Set<PsiStatement> {
        val result = hashSetOf<PsiStatement>()

        for (i in 0 until block.statementsCount) {
            block[i].accept(object : JavaRecursiveElementVisitor() {

                override fun visitStatement(statement: PsiStatement) {
                    super.visitStatement(statement)
                    if (statement.parent is PsiCodeBlock) {
                        result.add(statement)
                    }
                }
            })
        }
        return result
    }

    fun <T> getStatementsWithElement(block: BlockOfMethod, element: T): Int {
        val statementsMap = hashSetOf<PsiStatement>()
        var currentStatement: PsiStatement? = null

        for (i in 0 until block.statementsCount) {
            block[i].accept(object : JavaRecursiveElementVisitor() {

                override fun visitStatement(statement: PsiStatement?) {
                    val oldStatement = currentStatement
                    currentStatement = statement
                    super.visitStatement(statement)
                    currentStatement = oldStatement
                }

                override fun visitReferenceElement(reference: PsiJavaCodeReferenceElement) {
                    super.visitReferenceElement(reference)

                    if (reference.isReferenceTo(element as PsiElement)) {
                        statementsMap.add(currentStatement!!)
                    }
                }
            })
        }
        return statementsMap.count()
    }

    fun getBlockFromMethod(method: PsiMethod): BlockOfMethod {
        val statements = method.body!!.statements
        return BlockOfMethod(statements)
    }
}
