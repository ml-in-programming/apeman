package apeman_core.features_extraction.calculators.candidate

import apeman_core.base_entities.ExtractionCandidate
import apeman_core.base_entities.FeatureType
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiComment

class NumCommentsCandidate(candidates: List<ExtractionCandidate>
): AbstractNumCandidate(candidates, FeatureType.NUM_COMMENTS) {
    override fun createVisitor(methodCandidates: List<ExtractionCandidate>
    ) = object : CandidateVisitor(methodCandidates) {

        override fun visitComment(comment: PsiComment?) {
            super.visitComment(comment)
            if (comment!!.text != "/*{*/" && comment!!.text != "/*}*/")
                updateCounters()
        }
    }
}
