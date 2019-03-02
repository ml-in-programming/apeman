package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLiteralExpression;
import com.sixrr.stockmetrics.methodCalculators.NumSimpleElementCalculator;

public class NumLiteralsCalculator extends NumSimpleElementCalculator {

    @Override
    protected PsiElementVisitor createVisitor() {
        return new NumLiteralsCalculator.Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {

        @Override
        public void visitLiteralExpression(PsiLiteralExpression literal) {
            super.visitLiteralExpression(literal);
            elementsCounter++;
        }
    }
}
