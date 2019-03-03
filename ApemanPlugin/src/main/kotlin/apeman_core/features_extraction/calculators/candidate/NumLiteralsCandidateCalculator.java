package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLiteralExpression;

import java.util.ArrayList;

public class NumLiteralsCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumLiteralsCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_LITERAL);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumLiteralsCandidateCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        @Override
        public void visitLiteralExpression(PsiLiteralExpression literal) {
            super.visitLiteralExpression(literal);

            if (!isInsideMethod)
                return;
            updateCounters();
        }
    }
}
