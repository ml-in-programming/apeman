package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiSwitchStatement;
import com.sixrr.stockmetrics.methodCalculators.NumSimpleElementCalculator;

public class NumSwitchCalculator extends NumSimpleElementCalculator {

    @Override
    protected PsiElementVisitor createVisitor() {
        return new NumSwitchCalculator.Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            elementsCounter++;
        }
    }
}
