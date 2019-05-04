package apeman_core.features_extraction.calculators

import apeman_core.base_entities.ExtractionCandidate
import com.intellij.psi.*
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

typealias Cohesion = Pair<Double, Double>
typealias Coupling = Pair<Double, Double>

abstract class StatementsMap {

    protected val numElementsInStatement = linkedMapOf<PsiStatement, LinkedHashMap<PsiElement, Int>>()
    protected val numDescendantStatementsWithElement = linkedMapOf<PsiStatement, LinkedHashMap<PsiElement, Int>>()
    protected val statementsWithGivenElement = linkedMapOf<PsiElement, LinkedHashSet<PsiStatement>>()

    protected val numDescendantStatementsOverall = linkedMapOf<PsiStatement, Int>()
    protected val alreadySeenStatements = linkedSetOf<PsiStatement>()

    protected val allElementsSet = linkedSetOf<PsiElement>()
    protected val allElementsSorted = arrayListOf<PsiElement>()
    protected val stackOfCurrentStatements = ArrayList<PsiStatement>()

    var method: PsiMethod? = null

    protected open fun addElement(element: PsiElement) {
        if (stackOfCurrentStatements.isEmpty())
            return
        addElementToOverallSet(element)
        refreshNumberOfOccurrencesInStatements(element)
        refreshStatementsWhereOccurred(element)
    }

    private fun addElementToOverallSet(element: PsiElement) = allElementsSet.add(element)

    private fun refreshNumberOfOccurrencesInStatements(elem: PsiElement) {
        for (statement in stackOfCurrentStatements) {
            val numElements = numElementsInStatement.getOrPut(statement) { LinkedHashMap() }
            val count = numElements.getOrPut(elem) { 0 }
            numElements[elem] = count + 1
        }
    }

    private fun refreshStatementsWhereOccurred(element: PsiElement) {
        statementsWithGivenElement.getOrPut(element) { LinkedHashSet() }

        val elementStatement = stackOfCurrentStatements.last()

        if (statementsWithGivenElement[element]!!.contains(elementStatement))
            return
        statementsWithGivenElement[element]!!.add(elementStatement)

        for (statement in stackOfCurrentStatements) {
            val numStatementsWithElement = numDescendantStatementsWithElement.getOrPut(statement) { LinkedHashMap() }
            val numChildrenStatements = numStatementsWithElement.getOrDefault(element, 0)
            numStatementsWithElement[element] = numChildrenStatements + 1
        }
    }

    protected open fun addStatement() {
        // refresh numDescendantStatementsOverall for loc in Coupling

        var firstOccurrencesCount = 0
        for (statement in stackOfCurrentStatements.reversed()) {

            if (!alreadySeenStatements.contains(statement)) {
                alreadySeenStatements.add(statement)
                if (statement !is PsiBlockStatement)
                    firstOccurrencesCount++
            }
            numDescendantStatementsOverall.merge(statement, firstOccurrencesCount) { a, b -> a + b }
        }
    }

    open inner class Visitor : JavaRecursiveElementVisitor() {
        override fun visitStatement(statement: PsiStatement?) {
            stackOfCurrentStatements.add(statement!!)
            super.visitStatement(statement)
            addStatement()
            stackOfCurrentStatements.removeAt(stackOfCurrentStatements.count() - 1)
        }
    }

    fun addElementsAbstract() {
        method!!.accept(getVisitor())
        allElementsSorted.addAll(allElementsSet.sortedBy { it.text })
    }

    abstract fun getVisitor(): Visitor

    fun calculateNumAndCon(sourceCand: ExtractionCandidate, candidates: List<ExtractionCandidate>
    ): List<Pair<Int, Int>> {

        val numsAndCons = arrayListOf<Pair<Int, Int>>()
        val sourceElements = countElementsForCandidate(sourceCand)

        for (candidate in candidates) {
            val candidateElements = countElementsForCandidate(candidate)
            numsAndCons.add(candidateElements to sourceElements - candidateElements)
        }
        return numsAndCons
    }

    private fun countElementsForCandidate(candidate: ExtractionCandidate): Int {
        var overallElementsNum = 0
        for (statement in candidate.block) {
            val elementsInStatement = numElementsInStatement[statement]?.map { it.value }?.sum() ?: 0
            overallElementsNum += elementsInStatement
        }
        return overallElementsNum
    }

    fun calculateCouplingAndCohesions(
            sourceCand: ExtractionCandidate,
            candidates: List<ExtractionCandidate>
    ): List<Pair<Coupling, Cohesion>> {

        val couplingsAndCohesions = arrayListOf<Pair<Coupling, Cohesion>>()

        val sourceNums = allElementsSorted.asSequence()
                .map { it to numOfElementsForCand(sourceCand, it) }
                .toMap()

        for (candidate in candidates) {

            val ratio = linkedMapOf<PsiElement, Double>()
            for (elem in allElementsSorted) {
                val numCandidate = numOfElementsForCand(candidate, elem)
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
            candidate: ExtractionCandidate, elem: PsiElement, ratio: Map<PsiElement, Double>
    ): Pair<Double, Double> {
        val coup = ratio[elem] ?: 0.0
        val statementsWithElem = numOfStatementsForElementForCand(candidate, elem)
        val overallStatements = numStatementsInCandidate(candidate)
        val coh = statementsWithElem.toDouble() / overallStatements

        return coup to coh
    }

    protected open fun numOfElementsForCand(candidate: ExtractionCandidate, elem: PsiElement): Int {
        var overallCount = 0
        for (statement in candidate.block) {
            overallCount += numElementsInStatement[statement]?.get(elem) ?: 0
        }
        return overallCount
    }

    protected open fun numOfStatementsForElementForCand(candidate: ExtractionCandidate, elem: PsiElement): Int {
        var numStatements = 0
        for (statement in candidate.block) {
            numStatements += numDescendantStatementsWithElement[statement]?.get(elem) ?: 0
        }
        return numStatements
    }

    protected open fun numStatementsInCandidate(candidate: ExtractionCandidate): Int {
        var numStatements = 0
        for (statement in candidate.block) {
            numStatements += numDescendantStatementsOverall[statement]!!
        }
        return numStatements
    }

    protected open fun findMaxAndSecondMax(ratio: Map<PsiElement, Double>): Pair<PsiElement?, PsiElement?> {
        var firstMax = Double.MIN_VALUE
        var secondMax = Double.MIN_VALUE
        var firstElem: PsiElement? = null
        var secondElem: PsiElement? = null

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