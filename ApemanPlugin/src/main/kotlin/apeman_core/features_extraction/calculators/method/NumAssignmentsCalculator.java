package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAssignmentExpression;

public class NumAssignmentsCalculator extends NumSimpleElementCalculator {
    @Override
    public JavaRecursiveElementVisitor createVisitor() {
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
