package proof_of_concept

import apeman_core.utils.CandidateUtils
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.util.io.isFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.logging.Logger

class OracleParser(
        dirWithOracle: String,
        private val project: Project
) {
    private val log = Logger.getGlobal()

    private val scope = AnalysisScope(project)
    private val oraclePathStr = "$dirWithOracle/oracle.txt"
    private var entries = mutableListOf<OracleEntry>()

    fun parseOracle(): List<OracleEntry> {

//        val oraclePath = Paths.get(oraclePathStr)
//        assert(oraclePath.isFile())

        log.info("begin parsing")

        log.info("find all candidates")
        findCandidates()

        log.info("generate oracle")
        generateOracle()

        assert(entries.isNotEmpty())
        return entries
    }

    private fun findCandidates() {

        scope.accept(object : JavaRecursiveElementVisitor() {
            private var currentMethod: PsiMethod? = null
            private var startOffset = Stack<Int>()
            private var endOffset = Stack<Int>()
            private var nestingDepth = 0

            override fun visitMethod(method: PsiMethod?) {
                nestingDepth++
                if (nestingDepth == 1) {
                    currentMethod = method
                }

                super.visitMethod(method)

                if (nestingDepth == 1) {
                    currentMethod = null
                }
                nestingDepth--
            }

            override fun visitComment(comment: PsiComment?) {
                super.visitComment(comment)
                if (comment == null)
                    return

                if (comment.text!! == "/*{*/") {
                    assert(currentMethod != null)

                    startOffset.push(comment.textRange.startOffset)
                }

                if (comment.text!! == "/*}*/") {

                    assert(currentMethod != null)
                    assert(startOffset.isNotEmpty())

                    endOffset.push(comment.textRange.endOffset)
                    val candRange = TextRange(startOffset.pop(), endOffset.pop())
                    val candidate = CandidateUtils.fromTextRange(candRange, currentMethod!!) ?: return
                    val className = currentMethod!!.containingClass?.qualifiedName ?: ""
                    val methodName = getMethodSignature(currentMethod!!)
                    val methodSign = "$className\t$methodName"

                    entries.add(OracleEntry(
                            methodSign,
                            candRange.startOffset,
                            candRange.length,
                            currentMethod!!,
                            candidate
                    ))
                }
            }
        })
    }

    private fun getMethodSignature(method: PsiMethod): String {
        val methodName = method.name
        val modifiersString = method.modifierList.text.split("\n").lastOrNull { it.isNotBlank() }?.trim() ?: ""
        val returnType = method.returnType?.canonicalText ?: ""

        val parametersStr = method.parameterList.parameters
                .joinToString { it.type.canonicalText }

        var throws = method.throwsList.referencedTypes.joinToString { it.canonicalText }
        if (throws.isNotBlank())
            throws = "throws $throws"

        val line = "$modifiersString $returnType $methodName($parametersStr) $throws".trim()
        return line
    }

    private fun generateOracle() {
        Files.newBufferedWriter(Paths.get(oraclePathStr)).use {
            it.append(entries.joinToString("\n") { entry -> entry.methodName })
        }
    }
}
