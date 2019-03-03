package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLiteralExpression;

import java.util.ArrayList;

public class NumLiteralsMethodCalculator extends NumSimpleElementMethodCalculator {

    public NumLiteralsMethodCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_LITERAL);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumLiteralsMethodCalculator.Visitor();
    }

    private class Visitor extends NumSimpleElementMethodCalculator.Visitor {

        @Override
        public void visitLiteralExpression(PsiLiteralExpression literal) {
            super.visitLiteralExpression(literal);
            elementsCounter++;
        }
    }
}
