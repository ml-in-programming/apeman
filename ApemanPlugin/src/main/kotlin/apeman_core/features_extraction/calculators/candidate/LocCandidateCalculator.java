package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.FeatureType;
import apeman_core.features_extraction.calculators.BaseMetricsCalculator;
import apeman_core.pipes.CandidateWithFeatures;
import apeman_core.utils.BlocksUtils;
import apeman_core.utils.CandidateUtils;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class LocCandidateCalculator extends BaseMetricsCalculator {

    public LocCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.LOC_CANDIDATE);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new JavaRecursiveElementVisitor() {

            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                List<CandidateWithFeatures> candidatesOfMethod =
                        CandidateUtils.getCandidatesOfMethod(method, getCandidates());

                for (CandidateWithFeatures cand: candidatesOfMethod) {
                    getResults().set(cand, getFirstFeature(),
                            BlocksUtils.getNumStatementsRecursively(cand.getCandidate().getBlock()));
                }
            }
        };
    }
}
