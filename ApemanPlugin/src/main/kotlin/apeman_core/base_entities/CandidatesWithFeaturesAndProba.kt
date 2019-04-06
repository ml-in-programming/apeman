package apeman_core.base_entities

data class CandidatesWithFeaturesAndProba(
        val candidate: ExtractionCandidate,
        val features: Features = Features(FeatureType::class.java),
        val probability: Double
)
