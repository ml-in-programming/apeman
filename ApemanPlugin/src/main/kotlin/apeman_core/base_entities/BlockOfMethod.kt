package apeman_core.base_entities

import com.intellij.psi.PsiStatement

import java.util.Arrays

class BlockOfMethod(statements: Array<PsiStatement>) {
    private val statements: Array<PsiStatement>

    val firstStatement: PsiStatement
        get() = statements[0]

    val lastStatement: PsiStatement
        get() = statements[statements.size - 1]

    val statementsCount: Int
        get() = statements.size

    init {
        this.statements = Arrays.copyOf(statements, statements.size)
    }

    operator fun get(index: Int): PsiStatement {
        return statements[index]
    }
}
