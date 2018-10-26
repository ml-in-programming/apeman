import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement

data class Candidate(val start: PsiStatement, val end: PsiStatement, val sourceMethod: PsiMethod)