package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiConditionalExpression;

import java.util.ArrayList;

public class NumTernaryMethodCalculator extends NumSimpleElementMethodCalculator {

    public NumTernaryMethodCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_CONDITIONAL);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumTernaryMethodCalculator.Visitor();
    }

    private class Visitor extends NumSimpleElementMethodCalculator.Visitor {
        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            elementsCounter++;
        }
    }
}
