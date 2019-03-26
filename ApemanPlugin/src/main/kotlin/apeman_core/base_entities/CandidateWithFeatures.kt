package apeman_core.base_entities

data class CandidateWithFeatures(
        val candidate: ExtractionCandidate,
        val features: Features = Features(FeatureType::class.java)
)
