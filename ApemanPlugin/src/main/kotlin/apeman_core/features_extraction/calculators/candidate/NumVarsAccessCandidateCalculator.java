package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumVarsAccessCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumVarsAccessCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_VAR_ACCESS);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumVarsAccessCandidateCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        PsiMethod currentMethod = null;

        @Override
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0)
                currentMethod = method;

            super.visitMethod(method);
            currentMethod = null;
        }

        @Override
        public void visitLocalVariable(PsiLocalVariable variable) {
            super.visitLocalVariable(variable);
            if (isInsideMethod)
                updateCounters();
        }

        @Override
        public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
            super.visitReferenceElement(reference);
            PsiElement elem = reference.resolve();
            if (elem instanceof PsiLocalVariable && isInsideMethod &&
                    PsiTreeUtil.isAncestor(currentMethod, elem, true)) {
                updateCounters();
            }
        }
    }
}
