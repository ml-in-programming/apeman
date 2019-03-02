package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElementVisitor;

public class NumAssignmentsCalculator extends NumSimpleElementCalculator {
    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {

        @Override
        public void visitAssignmentExpression(PsiAssignmentExpression expression) {
            super.visitAssignmentExpression(expression);
            elementsCounter++;
        }
    }
}
