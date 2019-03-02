package apeman_core.base_entities

import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement

class ExtractionCandidate @JvmOverloads constructor(statements: Array<PsiStatement>, val sourceMethod: PsiMethod, uniqueId: Int = 0) {
    val block: BlockOfMethod
    var isInCandidate: Boolean = false
    val id: String

    val start: PsiStatement
        get() = block.firstStatement

    val end: PsiStatement
        get() = block.lastStatement

    init {
        this.block = BlockOfMethod(statements)
        this.id = "cand_$uniqueId"
    }

    override fun toString(): String {

        val str = StringBuilder()//"Candidate of ");
        //        str.append(sourceMethod).append(":\n");

        val blockSize = block.statementsCount
        for (i in 0 until blockSize) {
            str.append(block.get(i).text).append("\n")
            if (i != blockSize - 1)
                str.append("\n")
        }
        return str.toString()
    }
}
