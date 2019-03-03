package apeman_core.features_extraction.calculators.candidate;

import apeman_core.base_entities.BlockOfMethod;
import apeman_core.base_entities.ExtractionCandidate;
import apeman_core.base_entities.FeatureType;
import apeman_core.features_extraction.calculators.BaseMetricsCalculator;
import apeman_core.pipes.CandidateWithFeatures;
import apeman_core.utils.BlocksUtils;
import apeman_core.utils.CandidateUtils;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

public class RatioLocCandidateCalculator extends BaseMetricsCalculator {

    public RatioLocCandidateCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.LOC_RATIO);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new JavaRecursiveElementVisitor() {

            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                if (method.getBody() == null) // abstract method or interface
                    return;
                BlockOfMethod blockOfMethod = BlocksUtils.getBlockFromMethod(method);
                List<CandidateWithFeatures> candidatesOfMethod =
                        CandidateUtils.getCandidatesOfMethod(method, getCandidates());
                int numStatementsMethod = BlocksUtils.getNumStatementsRecursively(blockOfMethod);

                for (CandidateWithFeatures cand: candidatesOfMethod) {
                    int candStatements = BlocksUtils.getNumStatementsRecursively(cand.getCandidate().getBlock());
                    getResults().set(cand, getFirstFeature(),  candStatements / (double)numStatementsMethod);
                }
            }
        };
    }
}
