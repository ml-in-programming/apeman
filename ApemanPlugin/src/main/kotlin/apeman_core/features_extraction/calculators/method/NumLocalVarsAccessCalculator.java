package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;

public class NumLocalVarsAccessCalculator extends NumSimpleElementCalculator {

    public NumLocalVarsAccessCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_VAR_ACCESS);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
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
