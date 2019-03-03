package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLocalVariable;

public class NumLocalVarsCalculator extends NumSimpleElementCalculator {

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
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
