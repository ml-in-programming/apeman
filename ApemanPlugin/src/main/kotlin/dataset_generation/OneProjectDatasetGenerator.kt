package dataset_generation

import apeman_core.base_entities.CandidateWithFeatures
import apeman_core.base_entities.ExtractionCandidate
import apeman_core.features_extraction.FeaturesForEveryCandidate
import apeman_core.prediction.Csv
import apeman_core.prediction.SciKitModelProvider
import apeman_core.prediction.importCsvFrom
import apeman_core.utils.CandidateValidation
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.*
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
            getFeaturesAndMakeCsv()

            ProjectManager.getInstance().closeProject(project!!)
            log.info("generation success!")

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
        methods.forEach {
            generateNegativeCandidateForMethod(it, random)
            if (negativeCandidates.count() > 3600)
                return
        }
    }

    private fun generateNegativeCandidateForMethod(method: PsiMethod, random: Random) {
        var generated = true
        while (!generated) {
            method.accept(object : JavaRecursiveElementVisitor() {

                override fun visitCodeBlock(block: PsiCodeBlock?) {
                    super.visitCodeBlock(block)
                    if (random.nextInt(2) == 0 && !generated) {
                        generateNegativeCandidateForBlock(method, block!!, random)
                        generated = true
                    }
                }
            })
        }
    }

    private fun generateNegativeCandidateForBlock(method: PsiMethod, block: PsiCodeBlock, random: Random): Boolean {
        val n = block.statementCount
        if (n == 0) return false

        var tries = 0
        while (tries < 2) {
            val startInclusive = random.nextInt(n)
            val endInclusive = random.nextInt(n - startInclusive) + startInclusive
            val candidate = ExtractionCandidate(
                    Arrays.copyOfRange(block.statements, startInclusive, endInclusive + 1),
                    method,
                    isSourceCandidate = false,
                    positive = false
            )
            if (CandidateValidation.isValid(candidate, project!!)) {
                negativeCandidates.add(candidate)
                return true
            }
            tries++
        }
        return false
    }

    private fun getFeaturesAndMakeCsv() {
        var offset = 4400
        val limit = 400

        val methods = sourceCandidates.map { it.sourceMethod }.distinct()

        while (offset + limit < methods.count()) {

            log.info("start with offset: $offset")
            val ourMethods = methods.filterIndexed { index, psiMethod ->  offset <= index && index < offset + limit }
            val candidates = listOf(
                    positiveCandidates.filter { ourMethods.contains(it.sourceMethod) },
                    negativeCandidates.filter { ourMethods.contains(it.sourceMethod) },
                    sourceCandidates.filter { ourMethods.contains(it.sourceMethod) }
            ).flatten()

            val features = FeaturesForEveryCandidate(candidates).getCandidatesWithFeatures()
            makeCsv(features, offset)
            offset += limit
        }

    }

    private fun getFeatures(): List<CandidateWithFeatures> {
        val candidates = listOf(
                positiveCandidates, sourceCandidates, negativeCandidates
        ).flatten()

        return FeaturesForEveryCandidate(candidates).getCandidatesWithFeatures()
    }

    private fun makeCsv(featureCandidates: List<CandidateWithFeatures>, offset: Int) {
        log.info("make csv")

        val positiveCandidates = featureCandidates.filter { it.candidate.positive != null && it.candidate.positive }
        val columnNames = SciKitModelProvider(positiveCandidates).getColumnNames()

        val positiveCsv = importCsvFrom(positiveCandidates, columnNames)
        positiveCsv.export("$pathToProject/dataset_pos_$offset.csv")

        val negativeCandidates = featureCandidates.filter { it.candidate.positive != null && !it.candidate.positive }
        val negativeCsv = importCsvFrom(negativeCandidates, columnNames)
        negativeCsv.export("$pathToProject/dataset_neg_$offset.csv")
    }
}
