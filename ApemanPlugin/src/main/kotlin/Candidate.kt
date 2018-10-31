import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiStatement

class Candidate(val start: PsiStatement, val end: PsiStatement, val sourceMethod: PsiMethod) {
    override fun toString(): String {
        return "Candidate, textOffset: ${start.textOffset}..${end.textOffset}, text: \n" +
                "${start.text}\n..\n${end.text}\n"
    }
}