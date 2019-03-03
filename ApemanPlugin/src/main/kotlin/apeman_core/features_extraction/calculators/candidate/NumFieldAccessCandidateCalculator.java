package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.*;

import java.util.ArrayList;

public class NumFieldAccessCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumFieldAccessCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_FIELD_ACCESS);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumFieldAccessCandidateCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

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
            if (elem instanceof PsiField && isInsideMethod &&
                    ((PsiField) elem).getContainingClass() == currentClass) {
                updateCounters();
            }
        }
    }
}
