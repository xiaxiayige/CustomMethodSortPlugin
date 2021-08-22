package com.xiaxiayige.plugin.action.utils

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod


class Sorter(private val psiClass: PsiClass) {

    private val methods = HashMap<String, PsiMethod>()

    /***
     * 对方法排序
     */
    fun sort(rule: ArrayList<String>) {
        val resultMethods = LinkedHashMap<String, PsiMethod>()
        val currentFileMethods = psiClass.methods
        currentFileMethods.forEach { methods[it.name] = it }
        //排序
        getResultMethodsForRule(rule, resultMethods)
        resetKtNameFunctionOrder(resultMethods)
        deleteOldKtNameFunctionMetthod()
    }

    private fun getResultMethodsForRule(
        rule: ArrayList<String>,
        resultMethods: LinkedHashMap<String, PsiMethod>
    ) {
        rule.forEach { name ->
            methods.forEach { (methodName, psiMethod) ->
                if (name == methodName) {
                    resultMethods[name] = psiMethod
                }
            }
        }
        methods.forEach { t, u ->
            resultMethods[t] = u
        }
    }

    /***
     * 重新设置方法的顺序
     */
    private fun resetKtNameFunctionOrder(resultMethods: LinkedHashMap<String, PsiMethod>) {
        val psiElement: PsiElement = psiClass.methods[0]
        resultMethods.forEach {
            psiClass.addBefore(it.value, psiElement)
        }
    }

    /***
     * 删除原来的方法
     */
    private fun deleteOldKtNameFunctionMetthod() {
        methods.forEach { (_, u) ->
            u.delete()
        }
    }

}