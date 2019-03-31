package dataset_generation

import apeman_core.base_entities.CandidateWithFeatures
import apeman_core.base_entities.ExtractionCandidate
import apeman_core.features_extraction.FeaturesForEveryCandidate
import apeman_core.prediction.importCsvFrom
import apeman_core.utils.CandidateValidation
import com.intellij.analysis.AnalysisScope
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.*
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.refactoring.inline.InlineMethodProcessor
import proof_of_concept.OracleParser
import java.util.*
import java.util.logging.Logger

class OneProjectDatasetGenerator(
        private val pathToProject: String
) {
    private val log = Logger.getGlobal()!!
    private var project: Project? = null

    private val positiveCandidates = arrayListOf<ExtractionCandidate>()
    private val negativeCandidates = arrayListOf<ExtractionCandidate>()
    private val sourceCandidates = arrayListOf<ExtractionCandidate>()

    init {
        analyze()
    }

    private fun analyze() {
        try {
            log.fine("analyze project")
            log.info(pathToProject)

            log.info("load project")
            loadProject()

            CommandProcessor.getInstance().executeCommand(project, {
                log.info("inner methods")
                innerMethods()
            }, null, null)

            ProjectManager.getInstance().closeProject(project!!)
            loadProject()

            log.info("generate positive canidates")
            generatePositiveCandidates()

            log.info("generate source canidates")
            generateSourceCandidates()

            log.info("generate negative canidates")
            generateNegativeCandidates()

            log.info("calculate features and make csv")
            makeCsv(getFeatures())

            ProjectManager.getInstance().closeProject(project!!)

        } catch (e: Exception) {
            println(e)
        } catch (e: Error) {
            println(e)
        }
    }

    private fun makeCsv(featureCandidates: List<CandidateWithFeatures>) {
        val positiveAndNegative = featureCandidates.filter { !it.candidate.isSourceCandidate }

        val csv = importCsvFrom(positiveAndNegative, positiveAndNegative[0].features.keys.map { it.name })
        csv.export("$pathToProject/dataset.csv")
    }

    private fun getFeatures(): List<CandidateWithFeatures> {
        val candidates = listOf(
                positiveCandidates, sourceCandidates, negativeCandidates
        ).flatten()

        return FeaturesForEveryCandidate(candidates).getCandidatesWithFeatures()
    }

    private fun loadProject() {
        project = ProjectManager.getInstance().loadAndOpenProject(pathToProject)!!
    }

    private fun innerMethods() {
        val scope = AnalysisScope(project!!)
        scope.accept(object : JavaRecursiveElementVisitor() {
            override fun visitMethod(method: PsiMethod?) {
                super.visitMethod(method)

                val reference = getFirstReferenceOrNull(method!!) ?: return
                if (method.isConstructor || method.body == null || !method.isWritable)
                    return
                if (!method.isValid)
                    return
//                if (!InlineMethodProcessor.checkUnableToInsertCodeBlock())


                addBracketsToMethod(method)

                val editor = getEditor(reference)
                try {
                    InlineMethodProcessor(
                            reference.element.project, method, reference, editor, false
                    ).run()
                } catch (e: PsiInvalidElementAccessException) {
                    print(e)
                }
                EditorFactory.getInstance().releaseEditor(editor)
            }
        })
    }

    private fun getFirstReferenceOrNull(method: PsiMethod): PsiJavaCodeReferenceElement? {
        val query = ReferencesSearch.search(method)
        var totalRefs = 0
        var reference: PsiReference? = null
        query.forEach {
            if (totalRefs == 0) totalRefs++ else return null
            reference = it
        }
        return reference as? PsiJavaCodeReferenceElement
    }

    private fun getEditor(reference: PsiReference): Editor {
        val file = reference.element.containingFile
        val project = reference.element.project
        val document = PsiDocumentManager.getInstance(project).getDocument(file)!!
        return EditorFactory.getInstance().createEditor(document)!!
    }

    private fun addBracketsToMethod(sourceMethod: PsiMethod) {
        val factory = JavaPsiFacade.getInstance(project).getElementFactory()
        val startCandidateComment = factory.createCommentFromText("/*{*/", sourceMethod)
        val endCandidateComment = factory.createCommentFromText("/*}*/", sourceMethod)

        sourceMethod.body!!.addBefore(startCandidateComment, sourceMethod.body!!.firstBodyElement)
        sourceMethod.body!!.addAfter(endCandidateComment, sourceMethod.body!!.lastBodyElement)
    }

    private fun generatePositiveCandidates() {
        val entries = OracleParser(pathToProject, project!!).parseOracle()
        positiveCandidates.addAll(entries.map { it.candidate })
        CandidateValidation.releaseEditors()
    }

    private fun generateSourceCandidates() {
        assert(positiveCandidates.isNotEmpty())
        val methods = positiveCandidates.map { it.sourceMethod }.distinct()

        sourceCandidates.addAll(methods.map {
            ExtractionCandidate(it.body!!.statements, it, isSourceCandidate = true)
        })
    }

    private fun generateNegativeCandidates() {
        // for each method generate random valid candidate

        assert(sourceCandidates.isNotEmpty())
        val methods = sourceCandidates.map { it.sourceMethod }
        val random = Random(123L)
        methods.forEach {generateNegativeCandidateForMethod(it, random)}
    }

    private fun generateNegativeCandidateForMethod(method: PsiMethod, random: Random) {
        var generated = false
        while (!generated) {
            method.accept(object : JavaRecursiveElementVisitor() {

                override fun visitCodeBlock(block: PsiCodeBlock?) {
                    super.visitCodeBlock(block)

                    if (random.nextInt(3) == 0) {
                        generateNegativeCandidateForBlock(method, block!!, random)
                        generated = true
                    }

                }

            })
        }
    }

    private fun generateNegativeCandidateForBlock(method: PsiMethod, block: PsiCodeBlock, random: Random) {
        while (true) {
            val n = block.statementCount
            val startInclusive = random.nextInt(n - 1)
            val endInclusive = random.nextInt(n - startInclusive - 1) + startInclusive + 1
            val candidate = ExtractionCandidate(
                    Arrays.copyOfRange(block.statements, startInclusive, endInclusive + 1),
                    method,
                    isSourceCandidate = false
            )
            if (CandidateValidation.isValid(candidate)) {
                negativeCandidates.add(candidate)
                break
            }
        }
    }
}
