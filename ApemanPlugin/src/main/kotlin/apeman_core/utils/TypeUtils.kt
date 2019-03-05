package apeman_core.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiTreeUtil

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object TypeUtils {

    fun tryGetGetTypeMethod(element: PsiElement): Method? {
        var gettingTypeMethod: Method? = null
        try {
            gettingTypeMethod = element.javaClass.getMethod("getType")
        } catch (ignored: NoSuchMethodException) {
        } catch (ignored: SecurityException) {
        }

        return gettingTypeMethod
    }

    fun tryAddTypeOfElementTo(typeSet: MutableSet<PsiType>, element: PsiElement): Boolean {

        val isInMethod = PsiTreeUtil.getParentOfType(element, PsiMethod::class.java) != null
        if (!isInMethod)
            return false
        if (element is PsiMethod)
            return false

        val gettingTypeMethod = tryGetGetTypeMethod(element)

        if (gettingTypeMethod != null) {
            try {
                val type = gettingTypeMethod.invoke(element) as? PsiType
                if (type != null) {
                    typeSet.add(type)
                    return true
                }
                return false
            } catch (ignored: IllegalAccessException) {
            } catch (ignored: InvocationTargetException) {
            }

        }
        return false
    }

    fun addTypesFromMethodTo(typeSet: MutableSet<PsiType>, method: PsiMethod): Boolean {
        typeSet.add(method.returnType!!)
        val parameters = method.parameterList.parameters
        for (parameter in parameters) {
            typeSet.add(parameter.type)
        }
        return true
    }
}
