package apeman_core.features_extraction.calculators.method;

import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;

import java.util.ArrayList;

public class NumMethodCallsCalculator extends NumSimpleElementCalculator {

    public NumMethodCallsCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, CON);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            elementsCounter++;
        }

        @Override
        public void visitNewExpression(PsiNewExpression exp) {
            super.visitNewExpression(exp);
            if (exp.getArrayDimensions().length == 0 &&
                    exp.getArrayInitializer() == null) {
                elementsCounter++;
            }
        }
    }
}
