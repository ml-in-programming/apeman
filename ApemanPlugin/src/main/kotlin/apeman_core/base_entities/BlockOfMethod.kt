package apeman_core.base_entities

import com.intellij.psi.PsiStatement

import java.util.Arrays

class BlockOfMethod(statements: Array<PsiStatement>) : Iterable<PsiStatement>{
    private val statements = statements.copyOf(statements.size).map { it!! }

    val firstStatement = statements[0]

    val lastStatement = statements[statements.size - 1]
    val statementsCount = statements.size
    operator fun get(index: Int) = statements[index]

    public fun get() = statements

    override fun iterator(): Iterator<PsiStatement> = statements.iterator()
}
