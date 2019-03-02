package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.PsiIfStatement;

public class NumIfCalculator extends NumSimpleElementCalculator {

    class Visitor extends NumSimpleElementCalculator.Visitor {

        @Override
        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            elementsCounter++;
        }
    }
}
