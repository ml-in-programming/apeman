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
            log.info(pathToProject)
            log.info("load project")
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

    private fun loadProject() {
        project = ProjectManager.getInstance().loadAndOpenProject(pathToProject!!)!!
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

    private fun generateNegativeCandidateForBlock(method: PsiMethod, block: PsiCodeBlock, random: Random): Boolean {
        while (true) {
            val n = block.statementCount
            val startInclusive = random.nextInt(n)
            val endInclusive = random.nextInt(n - startInclusive) + startInclusive
            val candidate = ExtractionCandidate(
                    Arrays.copyOfRange(block.statements, startInclusive, endInclusive + 1),
                    method,
                    isSourceCandidate = false
            )
            if (CandidateValidation.isValid(candidate)) {
                negativeCandidates.add(candidate)
                return true
            }
        }
    }

    private fun getFeatures(): List<CandidateWithFeatures> {
        val candidates = listOf(
                positiveCandidates, sourceCandidates, negativeCandidates
        ).flatten()

        return FeaturesForEveryCandidate(candidates).getCandidatesWithFeatures()
    }

    private fun makeCsv(featureCandidates: List<CandidateWithFeatures>) {
        val positiveAndNegative = featureCandidates.filter { !it.candidate.isSourceCandidate }

        val csv = importCsvFrom(positiveAndNegative, positiveAndNegative[0].features.keys.map { it.name })
        csv.export("$pathToProject/dataset.csv")
    }
}
