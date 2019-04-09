package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiStatement
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

typealias Cohesion = Pair<Double, Double>
typealias Coupling = Pair<Double, Double>

abstract class StatementsMap {

    protected val elementsToCount = linkedMapOf<PsiStatement, LinkedHashMap<Any, Int>>()
    protected val elementsToNumStmts = linkedMapOf<PsiStatement, LinkedHashMap<Any, Int>>()
    protected val elementToStatements = linkedMapOf<Any, LinkedHashSet<PsiStatement>>()

    protected val statementsCount = linkedMapOf<PsiStatement, Int>()
    protected val uniqueStatements = linkedSetOf<PsiStatement>()

    protected val allElements = linkedSetOf<Any>()
    protected val allElementsList = arrayListOf<Any>()
    protected val statementsTrace = ArrayList<PsiStatement>()

    protected open fun addElem(elem: Any) {
        allElements.add(elem)

        // refresh elementsToCount for NUM, CON and COUPLING calculators
        for (statement in statementsTrace) {
            val elemToCount = elementsToCount.getOrPut(statement) { LinkedHashMap() }
            val count = elemToCount.getOrPut(elem) { 0 }
            elemToCount[elem] = count + 1
        }

        var numberOfUnique = 0
        elementToStatements.getOrPut(elem) { LinkedHashSet() }

        // refresh elementsToNumStmts for COHESION metric
        for (i in statementsTrace.count() - 1 downTo 0) {
            val statement = statementsTrace[i]

            if (!elementToStatements[elem]!!.contains(statement)) {
                elementToStatements[elem]!!.add(statement)
                numberOfUnique++
            }

            val elemToStmts = elementsToNumStmts.getOrPut(statement) { LinkedHashMap() }
            val statementsContains = elemToStmts.getOrDefault(elem, 0)
            elemToStmts[elem] = statementsContains + numberOfUnique
        }
    }

    protected open fun addStatement() {
        // refresh statementsCount for loc in Coupling

        var numberOfUnique = 0
        for (i in statementsTrace.count() - 1 downTo 0) {
            val statement = statementsTrace[i]

            if (!uniqueStatements.contains(statement)) {
                uniqueStatements.add(statement)
                numberOfUnique++
            }
            statementsCount.merge(statement, numberOfUnique) { a, b -> a + b }
        }
    }

    open inner class Visitor : JavaRecursiveElementVisitor() {
        override fun visitStatement(statement: PsiStatement?) {
            statementsTrace.add(statement!!)
            super.visitStatement(statement)
            addStatement()
            statementsTrace.removeAt(statementsTrace.count() - 1)
        }
    }

    fun addElementsAbstract(method: PsiElement) {
        method.accept(getVisitor())
        allElementsList.addAll(allElements.sortedBy { (it as PsiElement).text })
    }

    abstract fun getVisitor(): Visitor

    fun calculateNumAndCon(sourceCand: ExtractionCandidate, candidates: List<ExtractionCandidate>
    ): List<Pair<Int, Int>> {

        val numsAndCons = arrayListOf<Pair<Int, Int>>()
        var numSource = 0
        for (i in 0 until sourceCand.block.statementsCount) {
            val statement = sourceCand.block[i]
            numSource += elementsToCount[statement]?.map { it.value }?.sum() ?: 0
        }

        for (candidate in candidates) {
            var allNum = 0
            for (i in 0 until candidate.block.statementsCount) {
                val statement = candidate.block[i]
                val numElements = elementsToCount[statement]?.map { it.value }?.sum() ?: 0
                allNum += numElements
            }
            numsAndCons.add(allNum to numSource - allNum)
        }
        return numsAndCons
    }

    fun calculateCouplingAndCohesions(
            sourceCand: ExtractionCandidate,
            candidates: List<ExtractionCandidate>
    ): List<Pair<Coupling, Cohesion>> {

        val couplingsAndCohesions = arrayListOf<Pair<Coupling, Cohesion>>()

        val sourceNums = allElementsList
                .map { it to calculateNumOfConcreteElem(sourceCand, it) }
                .toMap()

        for (candidate in candidates) {

            val ratio = linkedMapOf<Any, Double>()
            for (elem in allElementsList) {
                val numCandidate = calculateNumOfConcreteElem(candidate, elem)
                ratio[elem] = numCandidate.toDouble() / sourceNums.getValue(elem)
            }
            val (firstElem, secondElem) = findMaxAndSecondMax(ratio)
            var coup1 = 0.0
            var coup2 = 0.0
            var coh1 = 0.0
            var coh2 = 0.0

            if (firstElem != null) {
                val pair = calculateCouplingAndCohesionForElem(candidate, firstElem, ratio)
                coup1 = pair.first
                coh1 = pair.second
            }

            if (secondElem != null) {
                val pair = calculateCouplingAndCohesionForElem(candidate, secondElem, ratio)
                coup2 = pair.first
                coh2 = pair.second
            }

            couplingsAndCohesions.add((coup1 to coup2) to (coh1 to coh2))
        }
        return couplingsAndCohesions
    }

    protected open fun calculateCouplingAndCohesionForElem(
            candidate: ExtractionCandidate, elem: Any, ratio: Map<Any, Double>
    ): Pair<Double, Double> {
        val coup = ratio[elem] ?: 0.0
        val statementsWithElem = calculateStatementsOfConcreteElem(candidate, elem)
        val overallStatements = calculateOverallStatements(candidate)
        val coh = statementsWithElem.toDouble() / overallStatements

        return coup to coh
    }

    protected open fun calculateNumOfConcreteElem(candidate: ExtractionCandidate, elem: Any): Int {
//        assert(elementsToCount.isNotEmpty())
        var concreteNum = 0
        for (i in 0 until candidate.block.statementsCount) {
            val statement = candidate.block[i]
            concreteNum += elementsToCount[statement]?.get(elem) ?: 0
        }
        return concreteNum
    }

    protected open fun calculateStatementsOfConcreteElem(candidate: ExtractionCandidate, elem: Any): Int {
//        assert(elementsToNumStmts.isNotEmpty())
        var numStatements = 0
        for (i in 0 until candidate.block.statementsCount) {
            val statement = candidate.block[i]
            numStatements += elementsToNumStmts[statement]?.get(elem) ?: 0
        }
        return numStatements
    }

    protected open fun calculateOverallStatements(candidate: ExtractionCandidate): Int {
//        assert(statementsCount.isNotEmpty())
        var numStatements = 0
        for (i in 0 until candidate.block.statementsCount) {
            val statement = candidate.block[i]
            numStatements += statementsCount[statement]!!
        }
        return numStatements
    }

    protected open fun findMaxAndSecondMax(ratio: Map<Any, Double>): Pair<Any?, Any?> {
        var firstMax = Double.MIN_VALUE
        var secondMax = Double.MIN_VALUE
        var firstElem: Any? = null
        var secondElem: Any? = null

        for ((elem, value) in ratio) {
            if (value > firstMax) {
                secondMax = firstMax
                firstMax = value

                secondElem = firstElem
                firstElem = elem
            } else if (value > secondMax) {
                secondMax = value
                secondElem = elem
            }
        }
        return firstElem to secondElem
    }
}