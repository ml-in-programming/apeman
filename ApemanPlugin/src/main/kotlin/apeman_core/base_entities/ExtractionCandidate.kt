package apeman_core.base_entities

import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement

class ExtractionCandidate(statements: Array<PsiStatement>,
                          val sourceMethod: PsiMethod,
                          val isSourceCandidate: Boolean = false,
                          var positive: Boolean? = null
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
        val start = block.firstStatement.textRange.startOffset
        val end = block.lastStatement.textRange.endOffset

        val sourceText = sourceMethod.text
        val sourceStart = sourceMethod.textRange.startOffset
        val sourceEnd = sourceMethod.textRange.endOffset

        return sourceText
                .substring(0, start - sourceStart) +
                "\n////BEGIN\n" +
                sourceText.substring(start - sourceStart, end - sourceStart) +
                "\n////END\n" +
                sourceText.substring(end - sourceStart)
    }
}
