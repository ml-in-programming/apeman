package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.features_extraction.calculators.BaseMetricsCalculator;
import apeman_core.pipes.CandidateWithFeatures;
import apeman_core.utils.CandidateUtils;
import apeman_core.utils.MethodUtils;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

abstract class AbstractNumCandidateCalculator extends BaseMetricsCalculator {

    public AbstractNumCandidateCalculator(ArrayList<CandidateWithFeatures> candidates, FeatureType feature) {
        super(candidates, feature);
    }

    public class CandidateVisitor extends JavaRecursiveElementVisitor {
        protected int methodNestingDepth = 0;
        ArrayList<CandidateWithFeatures> methodCandidates;
        ArrayList<Integer> counts = new ArrayList<>();
        boolean isInsideMethod = false;

        @Override
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                methodCandidates = CandidateUtils.getCandidatesOfMethod(method, getCandidates());
                initCounters();
                isInsideMethod = true;
            }

            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;

            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                for (int i = 0; i < methodCandidates.size(); i++) {
//                    postMetric(methodCandidates.get(i), getCounterForCand(i));
                }
                getCandidates().removeAll(methodCandidates);
                isInsideMethod = false;
            }
        }

        protected void initCounters() {
            counts.clear();
            methodCandidates.forEach(elem -> counts.add(0));
        }

        protected int getCounterForCand(int i) {
            return counts.get(i);
        }

        protected void updateCounters() {
            for (int i = 0; i < methodCandidates.size(); i++) {
                updateCounter(i);
            }
        }

        protected void updateCounter(int i) {
            if (methodCandidates.get(i).getCandidate().isInCandidate()) {
                counts.set(i, counts.get(i) + 1);
            }
        }

        @Override
        public void visitStatement(PsiStatement statement) {
            CandidateUtils.checkStartOfCandidates(statement, methodCandidates);
            super.visitStatement(statement);
            CandidateUtils.checkEndOfCandidates(statement, methodCandidates);
        }
    }
}
