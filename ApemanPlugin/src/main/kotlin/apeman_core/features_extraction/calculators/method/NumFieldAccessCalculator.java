package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.*;
import com.sixrr.stockmetrics.methodCalculators.NumSimpleElementCalculator;

public class NumFieldAccessCalculator extends NumSimpleElementCalculator {

    @Override
    protected PsiElementVisitor createVisitor() {
        return new NumFieldAccessCalculator.Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {
        PsiClass currentClass = null;

        @Override
        public void visitClass(PsiClass aClass) {
            currentClass = aClass;
            super.visitClass(aClass);
            currentClass = null;
        }

        @Override
        public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
            super.visitReferenceElement(reference);
            PsiElement elem = reference.resolve();
            if (elem instanceof PsiField && nestingDepth > 0 &&
                    ((PsiField)elem).getContainingClass() == currentClass
            ) {
                elementsCounter++;
            }
        }
    }
}
