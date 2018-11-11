import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLocalVariable
import com.intellij.psi.PsiRecursiveElementVisitor
import java.util.*


interface Features {
    fun generateFeatures(candidate: Candidate)

    //fun initFeatures() = FeaturesGems()
}

class FeaturesGems(val map: SortedMap<String, Double>, val candidate: Candidate) : Features {

    val candidateAndComplementNames = arrayListOf(
            "candidateMethod",
            "complementOfCandidate"
    )

    val featuresForMethods = arrayListOf(
            "LOC",
            "local",
            "literal",
            "invocation",
            "if",
            "ternary",
            "switch",
            "variableAccess",
            "typeAccess",
            "fieldAccess",
            "assert",
            "assign",
            "type",
            "package"
    )

    val ratioAndCohesionNames = arrayListOf("ratio", "cohesion")
    val ratioFeaturesNames = arrayListOf(
            "LOC",
            "variableAccess",
            "fieldAccess",
            "typeAccess",
            "package",
            "invocation",
            "type"
    )

    var CON_LOCAL by map
    var CON_LITERAL by map
    var CON_CONDITIONAL by map
    var CON_FIELD_ACC by map
    var CON_ASSERT by map
    var CON_ASSIGN by map
    var CON_TYPED_ELE by map
    var CON_PACKAGE by map
    var Num_local by map
    var Num_Literal by map
    var Num_Conditional by map
    var Num_Switch by map
    var Num_Var_Ac by map
    var Num_Type_Ac by map
    var Num_Field_Ac by map
    var Num_Assign by map
    var Num_Typed_Ele by map
    var Num_Package by map
    var ratio_LOC by map
    var Ratio_Variable_Access by map
    var Ratio_Variable_Access2 by map
    var VarAc_Cohesion by map
    var VarAc_Cohesion2 by map
    var Ratio_Field_Access by map
    var Ratio_Field_Access2 by map
    var Field_Cohesion by map
    var Field_Cohesion2 by map
    var Ratio_Invocation by map
    var Invocation_Cohesion by map
    var Ratio_Type_Access by map
    var Ratio_Type_Access2 by map
    var TypeAc_Cohesion by map
    var TypeAc_Cohesion2 by map
    var Ratio_Typed_Ele by map
    var TypedEle_Cohesion by map
    var Ratio_Package by map
    var Ratio_Package2 by map
    var Package_Cohesion by map
    var Package_Cohesion2 by map

    override fun generateFeatures(candidate: Candidate) {

    }

    private inline fun <reified CountedClass>countOfType(): Feature
            where CountedClass : PsiElement {

        var countInCandidate = 0
        var countInComplement = 0
        var isInCandidate = false

        candidate.sourceMethod.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement?) {
                super.visitElement(element)

                when (element) {
                    candidate.start -> isInCandidate = true
                    candidate.end -> isInCandidate = false
                    is CountedClass ->
                        if (isInCandidate) countInCandidate++ else countInComplement++
                }
            }
        })
        return Feature(countInCandidate, countInComplement)
    }
}