package apeman_core.features_extraction.calculators.candidate;

import apeman_core.utils.TypeUtils;
import com.intellij.psi.*;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;
import java.util.HashSet;

public class NumTypeAccessesCalculator extends AbstractNumCandidateCalculator {

    public NumTypeAccessesCalculator(ArrayList<ExtractionCandidate> candidates) {
        super(candidates);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumTypeAccessesCalculator.Visitor();
    }

    private class Visitor extends CandidateVisitor {

        ArrayList<HashSet<PsiType>> usedTypes;

        @Override
        protected void initCounters() {
            if (usedTypes == null) {
                usedTypes = new ArrayList<>();
            }
            usedTypes.clear();
            methodCandidates.forEach(cand -> usedTypes.add(new HashSet<>()));
        }

        @Override
        protected int getCounterForCand(int i) {
            return usedTypes.get(i).size();
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            if (!isInsideMethod)
                return;

            for (int i = 0; i < methodCandidates.size(); i++) {
                if (methodCandidates.get(i).isInCandidate()) {
                    TypeUtils.addTypesFromMethodTo(usedTypes.get(i), method);
                }
            }
        }

        @Override
        public void visitElement(PsiElement element) {
            super.visitElement(element);
            if (!isInsideMethod)
                return;

            for (int i = 0; i < methodCandidates.size(); i++) {
                if (methodCandidates.get(i).isInCandidate()) {
                    TypeUtils.tryAddTypeOfElementTo(usedTypes.get(i), element);
                }
            }
        }
    }
}
