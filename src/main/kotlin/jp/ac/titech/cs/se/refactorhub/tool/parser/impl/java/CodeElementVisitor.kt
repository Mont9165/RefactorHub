package jp.ac.titech.cs.se.refactorhub.tool.parser.impl.java

import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElement
import jp.ac.titech.cs.se.refactorhub.tool.model.element.data.Location
import jp.ac.titech.cs.se.refactorhub.tool.model.element.data.Range
import jp.ac.titech.cs.se.refactorhub.tool.model.element.impl.ClassDeclaration
import jp.ac.titech.cs.se.refactorhub.tool.model.element.impl.CodeFragment
import jp.ac.titech.cs.se.refactorhub.tool.model.element.impl.ConstructorDeclaration
import jp.ac.titech.cs.se.refactorhub.tool.model.element.impl.InterfaceDeclaration
import jp.ac.titech.cs.se.refactorhub.tool.model.element.impl.ParameterDeclaration
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.FieldDeclaration
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.eclipse.jdt.core.dom.MethodInvocation
import org.eclipse.jdt.core.dom.PackageDeclaration
import org.eclipse.jdt.core.dom.SingleVariableDeclaration
import org.eclipse.jdt.core.dom.Statement
import org.eclipse.jdt.core.dom.TypeDeclaration
import org.eclipse.jdt.core.dom.VariableDeclarationFragment
import org.eclipse.jdt.core.dom.VariableDeclarationStatement

class CodeElementVisitor(
    private val unit: CompilationUnit,
    private val path: String,
    val elements: MutableList<CodeElement> = mutableListOf()
) : ASTVisitor() {
    private var packageName = ""
    private var className = ""
    private var methodName = ""

    override fun visit(node: PackageDeclaration): Boolean {
        packageName = node.name.fullyQualifiedName
        return super.visit(node)
    }

    override fun visit(node: TypeDeclaration): Boolean {
        className = "$packageName.${node.name.identifier}"
        if (node.isInterface) {
            elements.add(
                InterfaceDeclaration(
                    className,
                    Location(path, node.range)
                )
            )
        } else {
            elements.add(
                ClassDeclaration(
                    className,
                    Location(path, node.range)
                )
            )
        }
        return super.visit(node)
    }

    override fun visit(node: FieldDeclaration): Boolean {
        val startPosition = node.startPosition
        node.fragments().forEach {
            val fragment = it as VariableDeclarationFragment
            elements.add(
                jp.ac.titech.cs.se.refactorhub.tool.model.element.impl.FieldDeclaration(
                    fragment.name.identifier,
                    className,
                    Location(path, getRange(startPosition, fragment.endPosition))
                )
            )
        }
        return super.visit(node)
    }

    override fun visit(node: MethodDeclaration): Boolean {
        if (node.isConstructor) {
            elements.add(
                ConstructorDeclaration(
                    node.name.identifier,
                    className,
                    Location(path, node.range)
                )
            )
        }
        elements.add(
            jp.ac.titech.cs.se.refactorhub.tool.model.element.impl.MethodDeclaration(
                node.name.identifier,
                className,
                Location(path, node.range)
            )
        )
        methodName = node.name.identifier
        node.parameters().forEach {
            val parameter = it as SingleVariableDeclaration
            elements.add(
                ParameterDeclaration(
                    parameter.name.identifier,
                    methodName,
                    className,
                    Location(path, parameter.range)
                )
            )
        }
        if (node.body != null && node.body.statements().isNotEmpty()) {
            val first = node.body.statements().first() as Statement
            val last = node.body.statements().last() as Statement
            elements.add(
                CodeFragment(
                    methodName,
                    className,
                    Location(path, getRange(first.startPosition, last.endPosition))
                )
            )
        }
        return super.visit(node)
    }

    override fun visit(node: MethodInvocation): Boolean {
        elements.add(
            jp.ac.titech.cs.se.refactorhub.tool.model.element.impl.MethodInvocation(
                methodName,
                className,
                Location(path, node.range)
            )
        )
        return super.visit(node)
    }

    override fun visit(node: VariableDeclarationStatement): Boolean {
        val startPosition = node.startPosition
        node.fragments().forEach {
            val fragment = it as VariableDeclarationFragment
            elements.add(
                jp.ac.titech.cs.se.refactorhub.tool.model.element.impl.VariableDeclaration(
                    fragment.name.identifier,
                    methodName,
                    className,
                    Location(path, getRange(startPosition, fragment.endPosition))
                )
            )
        }
        return super.visit(node)
    }

    private val ASTNode.endPosition: Int get() = this.startPosition + this.length

    private val ASTNode.range: Range
        get() = getRange(this.startPosition, this.endPosition)

    private fun getRange(startPosition: Int, endPosition: Int): Range = Range(
        unit.getLineNumber(startPosition),
        unit.getColumnNumber(startPosition) + 1,
        unit.getLineNumber(endPosition - 1),
        unit.getColumnNumber(endPosition - 1) + 1
    )
}
