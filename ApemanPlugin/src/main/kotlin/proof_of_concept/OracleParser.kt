package proof_of_concept

import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.fileTypes.StdFileTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.util.io.isFile
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.logging.Logger

class OracleParser(
        dirWithOracle: String,
        private val project: Project
) {
    private val log = Logger.getLogger("OracleParser")

    private val scope = AnalysisScope(project)
    private val oraclePathStr = "$dirWithOracle/oracle.txt"
    private var entries = mutableListOf<OracleEntry>()

    fun parseOracle(): List<OracleEntry> {
        log.info("begin parsing")
        val oraclePath = Paths.get(oraclePathStr)

        assert(oraclePath.isFile())

        Files.lines(oraclePath).forEach {
            parseLine(it)
        }
        log.info("find all methods from oracle")
        findAllMethods()

        log.info("create candidates")
        createCandidates()
        return entries
    }

    private fun parseLine(line: String) {

        val args = line.split("\t").map { it.trim() }
        val methodName = "${args[0]}\t${args[1]}"

        val startOffsets = arrayListOf<Int>()
        val lengths = arrayListOf<Int>()

        for (methodRange in args[2].split(';')) {
            if (methodRange.isNotBlank()) {
                val e = methodRange.split(":")
                val startOffset = e[0].drop(1).toInt()
                val lengthOffset = e[1].toInt()
                startOffsets.add(startOffset)
                lengths.add(lengthOffset)
            }
        }

        entries.add(OracleEntry(methodName, startOffsets, lengths))
    }

    private fun findAllMethods() {
        assert(entries.isNotEmpty())

        val methodsStr = entries.mapIndexed { i, entry -> i to entry.methodName }

        scope.accept(object : JavaRecursiveElementVisitor() {
            override fun visitMethod(method: PsiMethod?) {
                super.visitMethod(method)
                if (method == null || method.containingFile.fileType != StdFileTypes.JAVA)
                    return
                if (method.containingClass?.isInterface != false)
                    return

                val className = method.containingClass?.qualifiedName ?: ""
                val methodName = getMethodSignature(method)
                val indices = methodsStr
                        .filter { (_, entry) -> entry == "$className\t$methodName" }
                        .forEach { (i, _) ->
                            assert(entries[i].method == null)
                            log.info(i.toString())
                            entries[i].method = method
                        }
            }
        })
        assert(entries.all { it.method != null })
        entries.forEach {
            log.info("${it.methodName}, ${it.method!!.containingFile}")
            val doc = PsiDocumentManager.getInstance(project).getDocument(it.method!!.containingFile)
            it.startOffsets.zip(it.lengthOffsets).forEach { (start, length) ->
                log.info(doc!!.getText(TextRange(start, start + length)))
            }
            log.info("\n\n\n")
        }
    }

    private fun getMethodSignature(method: PsiMethod): String {
        val methodName = method.name
        val modifiersString = method.modifierList.text.split("\n").last { it.isNotBlank() }.trim()
        val returnType = method.returnType?.canonicalText ?: ""

        val parametersStr = method.parameterList.parameters
                .joinToString { it.type.canonicalText }

        var throws = method.throwsList.referencedTypes.joinToString { it.canonicalText }
        if (throws.isNotBlank())
            throws = "throws $throws"

        val line = "$modifiersString $returnType $methodName($parametersStr) $throws".trim()
        return line
    }

    private fun createCandidates() {
        for (entry in entries) {
            assert(entry.method != null)
            entry.method!!.accept(object : JavaRecursiveElementVisitor() {

                private var currentCodeBlock: PsiCodeBlock? = null
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

                override fun visitCodeBlock(block: PsiCodeBlock?) {
                    val oldBlock = currentCodeBlock
                    currentCodeBlock = block

                    super.visitCodeBlock(block)
                    currentCodeBlock = oldBlock
                }

                override fun visitComment(comment: PsiComment?) {
                    super.visitComment(comment)
                    if (comment == null)
                        return

                    if (comment.text!! == "/*{*/") {
                        assert(currentCodeBlock != null)
                        assert(currentMethod != null)

                        startOffset.push(comment.textRange.startOffset)
                        log.info("e$startOffset")
                    }

                    if (comment.text!! == "/*}*/") {
                        assert(currentCodeBlock != null)
                        assert(currentMethod != null)
                        assert(startOffset.isNotEmpty())

                        endOffset.push(comment.textRange.endOffset)
                        val candRange = TextRange(startOffset.pop(), endOffset.pop())
                        log.info(":" + candRange.length)

                        val candStatements = currentCodeBlock!!.statements
                                .filter { candRange.contains(it.textRange) }
                        val cand = ExtractionCandidate(candStatements.toTypedArray(), currentMethod!!)
                        log.info(cand.toString())

                        entries.find { it.method == currentMethod!! }!!.candidate = cand
                    }
                }
            })
        }
    }
}
