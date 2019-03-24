package apeman_core.base_entities

import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement

class ExtractionCandidate(statements: Array<PsiStatement>,
                          val sourceMethod: PsiMethod,
                          val isSourceCandidate: Boolean = false
) {
    init {
        assert(statements.count() > 0)
    }

    val block = BlockOfMethod(statements)
    var isInCandidate: Boolean = false

    val start: PsiStatement
        get() = block.firstStatement

    val end: PsiStatement
        get() = block.lastStatement

    override fun toString(): String {
        val statementsRange = (0 until block.statementsCount)
        return statementsRange.joinToString(separator = "\n") { i -> block[i].text }
    }
}
