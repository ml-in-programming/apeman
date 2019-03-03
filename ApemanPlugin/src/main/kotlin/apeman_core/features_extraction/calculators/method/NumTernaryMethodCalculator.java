package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiConditionalExpression;

public class NumTernaryMethodCalculator extends NumSimpleElementCalculator {
    @Override
    public JavaRecursiveElementVisitor createVisitor() {
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
