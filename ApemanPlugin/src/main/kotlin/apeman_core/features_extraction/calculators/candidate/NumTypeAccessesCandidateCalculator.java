package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import apeman_core.utils.TypeUtils;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.HashSet;

public class NumTypeAccessesCandidateCalculator extends AbstractNumCandidateCalculator {

    public NumTypeAccessesCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.NUM_TYPE_ACCESS);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new NumTypeAccessesCandidateCalculator.Visitor();
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
                if (methodCandidates.get(i).getCandidate().isInCandidate()) {
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
                if (methodCandidates.get(i).getCandidate().isInCandidate()) {
                    TypeUtils.tryAddTypeOfElementTo(usedTypes.get(i), element);
                }
            }
        }
    }
}
