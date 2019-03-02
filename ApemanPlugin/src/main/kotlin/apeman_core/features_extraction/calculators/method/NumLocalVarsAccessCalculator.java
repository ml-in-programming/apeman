package apeman_core.features_extraction.calculators.method;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

public class NumLocalVarsAccessCalculator extends NumSimpleElementCalculator {

    @Override
    protected PsiElementVisitor createVisitor() {
        return new NumLocalVarsAccessCalculator.Visitor();
    }

    private class Visitor extends NumSimpleElementCalculator.Visitor {
        PsiMethod currentMethod = null;

        @Override
        public void visitMethod(PsiMethod method) {
            if (nestingDepth == 0)
                currentMethod = method;
            super.visitMethod(method);
            currentMethod = null;
        }

        @Override
        public void visitLocalVariable(PsiLocalVariable variable) {
            super.visitLocalVariable(variable);
            elementsCounter++;
        }

        @Override
        public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
            super.visitReferenceElement(reference);
            PsiElement elem = reference.resolve();
            if (elem instanceof PsiLocalVariable &&
                    PsiTreeUtil.isAncestor(currentMethod, elem, true)) {
                elementsCounter++;
            }
        }
    }
}
