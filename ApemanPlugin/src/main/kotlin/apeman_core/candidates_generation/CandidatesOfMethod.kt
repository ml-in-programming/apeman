package apeman_core.candidates_generation

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.utils.CandidateUtils
import apeman_core.utils.CandidateValidation
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.psi.*
import com.intellij.refactoring.extractMethod.ExtractMethodHandler
import java.util.*
import kotlin.collections.ArrayList

class CandidatesOfMethod(private val sourceMethod: PsiMethod) {

    val candidates = ArrayList<ExtractionCandidate>()
    var sourceCodeBlock = false

    init {
        generateCandidates()
    }

    private fun generateCandidates() {
        sourceMethod.accept(object : JavaRecursiveElementVisitor() {
            override fun visitCodeBlock(block: PsiCodeBlock) {
                super.visitCodeBlock(block)
                if (block == sourceMethod.body)
                    sourceCodeBlock = true
                generateCandidatesOfOneBlock(block)
                sourceCodeBlock = false
            }
        })
    }

    private fun generateCandidatesOfOneBlock(block: PsiCodeBlock) {
        val n = block.statementCount
        val statements = block.statements

        for (i in 0 until n) {
            for (j in i until n) {

                val isSourceCand =  sourceCodeBlock && i == 0 && j == n - 1

                val candidateBlock = ExtractionCandidate(
                        Arrays.copyOfRange(statements, i, j + 1),
                        sourceMethod,
                        isSourceCand
                )
                if (CandidateValidation.isValid(candidateBlock)) {
                    candidates.add(candidateBlock)
                }
            }
        }
    }
}