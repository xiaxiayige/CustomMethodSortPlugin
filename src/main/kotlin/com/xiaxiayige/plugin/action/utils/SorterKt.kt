package com.xiaxiayige.plugin.action.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.j2k.isInSingleLine
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

/***
 * 对Kotlin文件排序
 */
class SorterKt(private val ktClass: KtClass) {

    /***
     * 原始的方法列表
     */
    private val methods = LinkedHashMap<String, KtNamedFunction>()

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
        //所有带有Override注解的方法
        val overrideListMethod = ArrayList<KtNamedFunction>()
        methods.forEach {
            if (it.value.modifierList?.hasModifier(KtTokens.OVERRIDE_KEYWORD) == true) {
                overrideListMethod.add(it.value)
            }
        }


        //1.添加规则中的方法
        rule.forEach { name ->
            methods.forEach { (funName, ktNameFunction) ->
                if (name == funName) {
                    resultMethods[name] = ktNameFunction
                }
            }
        }

        //2.添加重写的方法
        overrideListMethod.forEach { resultMethods[it.name ?: ""] = it }

        //3.添加剩余的方法
        methods.forEach { (t, u) -> resultMethods[t] = u }
    }

    /***
     * 获取原始的方法列表数据
     */
    private fun getOriginalMethods() {
        val currentFileMethods = ktClass.body?.functions
        currentFileMethods?.forEach { methods[it.name ?: ""] = it }
    }

    /***
     * 重新设置方法的顺序
     */
    private fun resetKtNameFunctionOrder(
        resultMethods: LinkedHashMap<String, KtNamedFunction>,
        psiElement: PsiElement
    ) {
        resultMethods.forEach {
            try {
                //通过添加非空注解的方式，解决2个单行方法中间没有空格换行的问题
                if (it.value.isInSingleLine()) {
                    val faName = FqName("NonNull")
                    it.value.addAnnotation(faName, "", "", null)
                }
                ktClass.addBefore(it.value, psiElement)
            } catch (e: IncorrectOperationException) {
                e.printStackTrace()
            }

        }
    }

    //删除原来的方法
    private fun deleteOldKtNameFunctionMetthod() {
        methods.forEach { (_, u) -> u.delete() }
    }

}