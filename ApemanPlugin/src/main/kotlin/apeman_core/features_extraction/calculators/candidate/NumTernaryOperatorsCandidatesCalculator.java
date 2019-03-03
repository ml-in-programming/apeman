package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiConditionalExpression;

import java.util.ArrayList;

public class NumTernaryOperatorsCandidatesCalculator extends AbstractNumCandidateCalculator {

    public NumTernaryOperatorsCandidatesCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_CONDITIONAL);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumTernaryOperatorsCandidatesCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);

            if (!isInsideMethod)
                return;
            updateCounters();
        }
    }
}
