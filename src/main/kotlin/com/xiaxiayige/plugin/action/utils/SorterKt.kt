package com.xiaxiayige.plugin.action.utils

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

/***
 * 对Kotlin文件排序
 */
class SorterKt(val ktClass: KtClass) {

    /***
     * 原始的方法列表
     */
    private val originalMethods = HashMap<String, KtNamedFunction>()

    /***
     * 对方法排序
     */
    fun sort(rule: ArrayList<String>) {
        //存放最后排序后的的集合
        val resultMethods = LinkedHashMap<String, KtNamedFunction>()

        getOriginalMethods()

        getResultMethodsForRule(rule, resultMethods)

        val psiElement: PsiElement = ktClass.body!!.functions[0].originalElement
        //重新设置代码方法顺序

        resetKtNameFunctionOrder(resultMethods, psiElement)

        deleteOldKtNameFunctionMetthod()
    }

    /**
     * 根据规则，获取排序后的方法列表
     */
    private fun getResultMethodsForRule(
        rule: ArrayList<String>,
        resultMethods: LinkedHashMap<String, KtNamedFunction>
    ) {
        //排序,先将符合规则内的方法添加到结果集合中
        rule.forEach { name ->
            originalMethods.forEach { (funName, ktNameFunction) ->
                if (name == funName) {
                    resultMethods[name] = ktNameFunction
                }
            }
        }
        //将剩余的方法添加到末尾
        originalMethods.forEach { (t, u) -> resultMethods[t] = u }
    }

    /***
     * 获取原始的方法列表数据
     */
    private fun getOriginalMethods() {
        val currentFileMethods = ktClass.body?.functions
        currentFileMethods?.forEach { originalMethods[it.name ?: ""] = it }
    }

    /***
     * 重新设置方法的顺序
     */
    private fun resetKtNameFunctionOrder(
        resultMethods: LinkedHashMap<String, KtNamedFunction>,
        psiElement: PsiElement
    ) {
        resultMethods.forEach { ktClass.addBefore(it.value, psiElement) }
    }

    //删除原来的方法
    private fun deleteOldKtNameFunctionMetthod() {
        originalMethods.forEach { (_, u) -> u.delete() }
    }

}