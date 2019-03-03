package apeman_core.features_extraction.calculators.candidate;

import com.intellij.psi.*;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class NumFieldAccessCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumFieldAccessCandidateCalculator(ArrayList<ExtractionCandidate> candidates) {
        super(candidates);
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
