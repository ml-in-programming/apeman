package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;

abstract class NumSimpleElementCalculator extends MethodCalculator {

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    class Visitor extends JavaRecursiveElementVisitor {
        int elementsCounter = 0;
        int nestingDepth = 0;

        @Override
        public void visitMethod(PsiMethod method) {
            if (nestingDepth == 0) {
                elementsCounter = 0;
            }
            nestingDepth++;
            super.visitMethod(method);
            nestingDepth--;
            if (nestingDepth == 0) {
                postMetric(method, elementsCounter);
            }
        }
    }
}
