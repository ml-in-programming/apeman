package proof_of_concept

import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.util.io.isFile
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.todo

class OracleParser(
        dirWithOracle: String,
        private val project: Project,
        private val scope: AnalysisScope
) {
    private val oraclePathStr = "$dirWithOracle/oracle.txt"
    private var entries = mutableListOf<OracleEntry>()

    init {
        parseOracle()
    }

    fun parseOracle() {
        val oraclePath = Paths.get(oraclePathStr)

        assert(oraclePath.isFile())
        Files.lines(oraclePath).forEach {
            parseLine(it)
        }
        findAllMethods()
        createCandidates()
    }

    private fun parseLine(line: String) {
        val args = line.split("\t")
        val methodName = "${args[0]}\t${args[1]}"
        val e = args[2]
        entries.add(OracleEntry(methodName, e))
    }

    private fun findAllMethods() {
        assert(entries.isNotEmpty())

        val methodsStr = entries.map { it.methodName }

        scope.accept(object : JavaRecursiveElementVisitor() {
            override fun visitMethod(method: PsiMethod?) {
                if (method == null)
                    return

                val methodName = getMethodSignature(method)
                val className = method.containingClass!!.qualifiedName!!
                val ind = methodsStr.indexOf("$className\t$methodName")
                if (ind != -1) {
                    entries[ind].method = method
                }
                super.visitMethod(method)
            }
        })
    }

    private fun getMethodSignature(method: PsiMethod): String {
        val methodName = method.name

        val modifiers = method.modifierList
        val modifiersString = modifiers.children
                .filter { it is PsiKeyword }
                .map{ it.text }
                .joinToString(separator = " ")

        val returnType = method.returnTypeElement!!.text!!
        val parameters = method.parameterList.parameters
        val parametersStr = parameters.joinToString { it.typeElement!!.text }

        var throws = method.throwsTypes.joinToString { it.name }
        if (throws.isNotBlank())
            throws = "throws $throws"

        val line = "$modifiersString $returnType $methodName($parametersStr) $throws"
        return line
    }

    private fun createCandidates() {
        for (entry in entries) {
            assert(entry.method != null)
            //todo
        }
    }
}
