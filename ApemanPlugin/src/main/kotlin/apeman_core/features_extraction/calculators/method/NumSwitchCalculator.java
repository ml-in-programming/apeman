package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiSwitchStatement;

public class NumSwitchCalculator extends NumSimpleElementCalculator {

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
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
