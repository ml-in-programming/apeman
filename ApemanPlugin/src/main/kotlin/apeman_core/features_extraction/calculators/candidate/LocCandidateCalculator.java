package apeman_core.features_extraction.calculators.candidate;

import apeman_core.features_extraction.calculators.BaseMetricsCalculator;
import apeman_core.utils.BlocksUtils;
import apeman_core.utils.CandidateUtils;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;

public class LocCandidateCalculator extends BaseMetricsCalculator {

    private ArrayList<ExtractionCandidate> candidates;

    public LocCandidateCalculator(ArrayList<ExtractionCandidate> candidates) {
        this.candidates = new ArrayList<>(candidates);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new JavaRecursiveElementVisitor() {

            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                ArrayList<ExtractionCandidate> candidatesOfMethod =
                        CandidateUtils.getCandidatesOfMethod(method, candidates);

                for (ExtractionCandidate cand: candidatesOfMethod) {
//                    postMetric(cand, BlocksUtils.getNumStatementsRecursively(cand.getBlock()));
                }
            }
        };
    }
}
