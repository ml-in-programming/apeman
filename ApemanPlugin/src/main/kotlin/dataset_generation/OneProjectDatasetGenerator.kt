package dataset_generation

import apeman_core.base_entities.CandidateWithFeatures
import apeman_core.base_entities.ExtractionCandidate
import apeman_core.candidates_generation.CandidatesOfMethod
import apeman_core.features_extraction.FeaturesForEveryCandidate
import apeman_core.prediction.SciKitModelProvider
import apeman_core.prediction.importCsvFrom
import apeman_core.utils.CandidateValidation
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.*
import handleError
import handleException
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
            var offset = 0
            val limit = 1000
            loadProject()
            generatePositiveCandidates(offset, limit = 100000)
            val overallSize = positiveCandidates.count()
            closeProject()

            while (true) {
                positiveCandidates.clear()
                negativeCandidates.clear()
                sourceCandidates.clear()

                loadProject()

                log.info("generate positive canidates")
                generatePositiveCandidates(offset, limit)

                log.info("generate source canidates")
                generateSourceCandidates()

                log.info("generate negative canidates")
                generateNegativeCandidates()

                log.info("calculate features and make csv")
                getFeaturesAndMakeCsv(offset)

                log.info("generation success!")
                closeProject()

                offset += limit
                if (offset > overallSize)
                    return
            }

        } catch (e: Exception) {
            println(e)
            handleException(e)
        } catch (e: Error) {
            println(e)
            handleError(e)
        }
    }

    private fun loadProject() {
        project = ProjectManager.getInstance().loadAndOpenProject(pathToProject!!)!!
    }

    private fun closeProject() = ProjectManager.getInstance().closeProject(project!!)


    private fun generatePositiveCandidates(offset: Int, limit: Int) {
        val entries = OracleParser(pathToProject, project!!).parseOracle()
        positiveCandidates.addAll(
                entries.asSequence()
                .map { it.candidate }
                .filterIndexed { index, candidate ->
                    offset <= index && index <= offset + limit
                }
        )
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
        methods.forEachIndexed { index, psiMethod ->
            generateNegativeCandidateForMethod(psiMethod, random)
            if (index % 50 == 0) {
                log.info("${index.toDouble() / methods.count()}%, $index")
                CandidateValidation.releaseEditors()
            }
//            if (negativeCandidates.count() > 3600)
//                return
        }
    }

    private fun generateNegativeCandidateForMethod(method: PsiMethod, random: Random) {
        method.accept(object : JavaRecursiveElementVisitor() {
            var nestingDepth = 0

            override fun visitMethod(method: PsiMethod) {
                nestingDepth++
                super.visitMethod(method)
                nestingDepth--

                if (nestingDepth == 0) {
                    val methodCandidates = CandidatesOfMethod(method, project!!).candidates
                    if (methodCandidates.isEmpty())
                        return
                        val candIndex = random.nextInt(methodCandidates.count())
                    negativeCandidates.add(methodCandidates[candIndex].also { it.positive = false })
                }
            }
        })
    }

//    private fun generateNegativeCandidateForBlock(method: PsiMethod, block: PsiCodeBlock, random: Random): Boolean {
//        val n = block.statementCount
//        if (n == 0) return false
//
//        var tries = 0
//        while (tries < 2) {
//            val startInclusive = random.nextInt(n)
//            val endInclusive = random.nextInt(n - startInclusive) + startInclusive
//            val candidate = ExtractionCandidate(
//                    Arrays.copyOfRange(block.statements, startInclusive, endInclusive + 1),
//                    method,
//                    isSourceCandidate = false,
//                    positive = false
//            )
//            if (CandidateValidation.isValid(candidate, project!!)) {
//                negativeCandidates.add(candidate)
//                return true
//            }
//            tries++
//        }
//        return false
//    }

    private fun getFeaturesAndMakeCsv(outerOffset: Int) {
        var offset = 0
        val limit = 500

        val methods = sourceCandidates.map { it.sourceMethod }.distinct()

        while (offset < methods.count()) {

            log.info("start with offset: $outerOffset")
            val ourMethods = methods.filterIndexed { index, psiMethod ->  offset <= index && index < offset + limit }
            val candidates = listOf(
                    positiveCandidates.filter { ourMethods.contains(it.sourceMethod) },
                    negativeCandidates.filter { ourMethods.contains(it.sourceMethod) },
                    sourceCandidates.filter { ourMethods.contains(it.sourceMethod) }
            ).flatten()

            val features = FeaturesForEveryCandidate(candidates).getCandidatesWithFeatures()
            makeCsv(features, offset + outerOffset)
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

        val positiveCandidates = featureCandidates.filter { it.candidate.positive != null && it.candidate.positive!! }
        val columnNames = SciKitModelProvider(positiveCandidates).getColumnNames()

        val positiveCsv = importCsvFrom(positiveCandidates, columnNames)
        positiveCsv.export("$pathToProject/dataset_pos_$offset.csv")

        val negativeCandidates = featureCandidates.filter { it.candidate.positive != null && !it.candidate.positive!! }
        val negativeCsv = importCsvFrom(negativeCandidates, columnNames)
        negativeCsv.export("$pathToProject/dataset_neg_$offset.csv")
    }
}
