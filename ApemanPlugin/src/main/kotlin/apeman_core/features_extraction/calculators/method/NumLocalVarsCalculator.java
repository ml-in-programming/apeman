package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLocalVariable;
import com.sixrr.stockmetrics.methodCalculators.NumSimpleElementCalculator;

public class NumLocalVarsCalculator extends NumSimpleElementCalculator {

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {

        @Override
        public void visitLocalVariable(PsiLocalVariable variable) {
            super.visitLocalVariable(variable);
            elementsCounter++;
        }
    }
}
