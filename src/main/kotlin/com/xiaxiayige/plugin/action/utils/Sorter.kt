package com.xiaxiayige.plugin.action.utils

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.xiaxiayige.plugin.action.const.METHOD_ANNOTATION_NAME_OVERRIDE

class Sorter(private val psiClass: PsiClass) {

    private val methods = LinkedHashMap<String, PsiMethod>()

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
        deleteOldKtNameFunctionMethod()
    }

    private fun getResultMethodsForRule(
        rule: ArrayList<String>,
        resultMethods: LinkedHashMap<String, PsiMethod>
    ) {
        //所有带有Override注解的方法
        val overrideListMethod = ArrayList<PsiMethod>()
        methods.forEach { (_, value) ->
            if (value.annotations.any { it.text == METHOD_ANNOTATION_NAME_OVERRIDE }) {
                overrideListMethod.add(value)
            }
        }

        // TODO: 2021/8/25 将构造方法排在最前面

        //1.添加规则中的方法
        rule.forEach { name ->
            methods.forEach { (methodName, psiMethod) ->
                if (name == methodName) {
                    resultMethods[name] = psiMethod
                }
            }
        }

        //2.添加重写的方法
        overrideListMethod.forEach { resultMethods[it.name] = it }

        //3.添加剩余的方法
        methods.forEach { (name, psiMethod) -> resultMethods[name] = psiMethod }

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
    private fun deleteOldKtNameFunctionMethod() {
        methods.forEach { (_, u) -> u.delete() }
    }

}