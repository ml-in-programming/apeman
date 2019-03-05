package apeman_core.utils

import com.intellij.psi.*
import com.intellij.psi.search.searches.SuperMethodsSearch
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod
import com.intellij.util.Processor
import com.intellij.util.Query
import org.apache.log4j.Logger

import java.util.*
import java.util.regex.Pattern

object MethodUtils {
    fun isAbstract(method: PsiMethod): Boolean {
        if (method.hasModifierProperty(PsiModifier.STATIC) || method.hasModifierProperty(PsiModifier.DEFAULT)) {
            return false
        }
        if (method.hasModifierProperty(PsiModifier.ABSTRACT)) {
            return true
        }
        val containingClass = method.containingClass
        return containingClass != null && containingClass.isInterface
    }
}
