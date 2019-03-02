package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.PsiConditionalExpression;
import com.intellij.psi.PsiElementVisitor;

public class NumTernaryMethodCalculator extends NumSimpleElementCalculator {
    @Override
    protected PsiElementVisitor createVisitor() {
        return new NumTernaryMethodCalculator.Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {
        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            elementsCounter++;
        }
    }
}
