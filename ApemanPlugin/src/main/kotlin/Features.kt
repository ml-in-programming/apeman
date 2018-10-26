import java.util.*
import kotlin.collections.ArrayList

interface Features {
    fun generateFeatures(candidate: Candidate)

    //fun initFeatures() = FeaturesGems()
}

class FeaturesGems(val map: SortedMap<String, Double>) : Features {

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

    val CON_LITERAL by map
    val CON_CONDITIONAL by map
    val CON_FIELD_ACC by map
    val CON_ASSERT by map
    val CON_ASSIGN by map
    val CON_TYPED_ELE by map
    val CON_PACKAGE by map
    val Num_local by map
    val Num_Literal by map
    val Num_Conditional by map
    val Num_Switch by map
    val Num_Var_Ac by map
    val Num_Type_Ac by map
    val Num_Field_Ac by map
    val Num_Assign by map
    val Num_Typed_Ele by map
    val Num_Package by map
    val ratio_LOC by map
    val Ratio_Variable_Access by map
    val Ratio_Variable_Access2 by map
    val VarAc_Cohesion by map
    val VarAc_Cohesion2 by map
    val Ratio_Field_Access by map
    val Ratio_Field_Access2 by map
    val Field_Cohesion by map
    val Field_Cohesion2 by map
    val Ratio_Invocation by map
    val Invocation_Cohesion by map
    val Ratio_Type_Access by map
    val Ratio_Type_Access2 by map
    val TypeAc_Cohesion by map
    val TypeAc_Cohesion2 by map
    val Ratio_Typed_Ele by map
    val TypedEle_Cohesion by map
    val Ratio_Package by map
    val Ratio_Package2 by map
    val Package_Cohesion by map
    val Package_Cohesion2 by map

    override fun generateFeatures(candidate: Candidate) {
        TODO()
    }
}