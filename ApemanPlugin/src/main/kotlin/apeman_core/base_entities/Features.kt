package apeman_core.base_entities

import java.util.*

enum class FeatureType {
    NUM_LOC,
    LOC_RATIO,
    CON_LOC,

    NUM_LITERAL,
    NUM_CONDITIONAL,
    NUM_SWITCH,
    NUM_IF,
    NUM_ASSIGN,
    NUM_LOCAL,
    NUM_ASSERT,

    CON_ASSERT,
    CON_LITERAL,
    CON_CONDITIONAL,
    CON_SWITCH,
    CON_IF,
    CON_ASSIGN,
    CON_LOCAL,

    NUM_VAR_ACCESS,
    CON_VAR_ACCESS,
    VAR_ACCESS_COUPLING,
    VAR_ACCESS_COUPLING_2,
    VAR_ACCESS_COHESION,
    VAR_ACCESS_COHESION_2,

    NUM_FIELD_ACCESS,
    CON_FIELD_ACCESS,
    FIELD_ACCESS_COUPLING,
    FIELD_ACCESS_COUPLING_2,
    FIELD_ACCESS_COHESION,
    FIELD_ACCESS_COHESION_2,

    NUM_INVOCATION,
    CON_INVOCATION,
    INVOCATION_COUPLING,
    INVOCATION_COHESION,

    NUM_TYPED_ELEMENTS,
    CON_TYPED_ELEMENTS,
    TYPED_ELEMENTS_COUPLING,
    TYPED_ELEMENTS_COHESION,

    NUM_TYPE_ACCESS,
    CON_TYPE_ACCESS,
    TYPE_ACCESS_COUPLING,
    TYPE_ACCESS_COUPLING_2,
    TYPE_ACCESS_COHESION,
    TYPE_ACCESS_COHESION_2,

    NUM_PACKAGE,
    CON_PACKAGE,
    PACKAGE_COUPLING,
    PACKAGE_COUPLING_2,
    PACKAGE_COHESION,
    PACKAGE_COHESION_2;

    fun complementFeature(): FeatureType? {
        val replace = if (this.name.startsWith("NUM_"))
            "NUM_" to "CON_"
        else
            "CON_" to "NUM_"

        val complementName = this.name.replace(replace.first, replace.second)
        return try {
            FeatureType.valueOf(complementName)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

fun couplingCohesionGroup(name: String): List<FeatureType> {
    return FeatureType
            .values()
            .filter { it.name.startsWith(name) }
            .sortedBy { it.name }
}

typealias Features = EnumMap<FeatureType, Double>
