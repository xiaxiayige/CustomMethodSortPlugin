package com.xiaxiayige.plugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.xiaxiayige.plugin.action.utils.Sorter
import com.xiaxiayige.plugin.action.utils.SorterKt
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader


/***
 * 自定义方法排序
 */
class CostomMethodSortAction : AnAction() {

    private val SORTRULEFILENAME = "sortMethod.rule"
    private val FILE_TYPE_JAVA = "java"
    private val FILE_TYPE_KOTLIN = "kt"

    override fun actionPerformed(event: AnActionEvent) {
        val psiFile = event.getData(LangDataKeys.PSI_FILE)
        val isJavaFile = isTargetFileType(psiFile, FILE_TYPE_JAVA)
        val isKtFile = isTargetFileType(psiFile, FILE_TYPE_KOTLIN)
        val methodsRule: ArrayList<String> = getMethodRule(event.project)

        if (methodsRule.isNotEmpty()) {
            if (isJavaFile) {
                val psiClass = getPsiClassFromContext(event)
                sortMethods(psiClass, methodsRule);
            } else if (isKtFile) {
                val ktClass = getKtClassFromContext(event) as KtClass
                sortKtMethods(ktClass, methodsRule)
            }
        } else {
            print("not add method rule file , please Add sortMethod.rule file ")
        }


    }

    private fun isTargetFileType(psiFile: PsiFile?, targetType: String) = psiFile?.name?.endsWith(targetType, true)
        ?: false

    private fun getMethodRule(project: Project?): ArrayList<String> {
        val methodNames = ArrayList<String>()
        val projectFilePath = project?.basePath
        val file = File(projectFilePath, SORTRULEFILENAME)
        if (file.exists()) {
            val reader = BufferedReader(InputStreamReader(FileInputStream(file)))
            while (reader.ready()) {
                val methodName: String = reader.readLine()
                methodNames.add(methodName)
            }
        }
        return methodNames
    }

    /**
     * abort java
     */
    private fun sortMethods(psiClass: PsiClass?, methodsRule: ArrayList<String>) {
        psiClass?.let {
            WriteCommandAction.runWriteCommandAction(psiClass.project) {
                Sorter(psiClass).sort(methodsRule)
            }
        }

    }

    /**
     * abort kotlin
     */
    private fun sortKtMethods(ktClass: KtClass?, methodsRule: ArrayList<String>) {
        ktClass?.let {
            WriteCommandAction.runWriteCommandAction(ktClass.project) {
                SorterKt(ktClass).sort(methodsRule)

                //格式化代码
                val psiManager = PsiManager.getInstance(ktClass.project)
                val codeStyleManager = CodeStyleManager.getInstance(psiManager)
                codeStyleManager.reformat(ktClass)
            }
        }

    }

    /**
     * @param e the action event that occurred
     * @return The PSIClass object based on which class your mouse cursor was in
     */
    private fun getPsiClassFromContext(e: AnActionEvent): PsiClass? {
        val psiFile = e.getData(LangDataKeys.PSI_FILE)
        val editor = e.getData(PlatformDataKeys.EDITOR)
        if (psiFile == null || editor == null) {
            return null
        }
        val offSet = editor.caretModel.offset
        val elementAt = psiFile.findElementAt(offSet)
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass::class.java)
    }

    /**
     * @param e the action event that occurred
     * @return The PSIClass object based on which class your mouse cursor was in
     */
    private fun getKtClassFromContext(e: AnActionEvent): KtClass? {
        val psiFile = e.getData(LangDataKeys.PSI_FILE)
        val childrenOfType = psiFile?.getChildrenOfType<KtClass>()?.asList()
        return childrenOfType?.get(0)

    }


}