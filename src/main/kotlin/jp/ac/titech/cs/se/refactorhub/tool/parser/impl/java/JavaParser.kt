package jp.ac.titech.cs.se.refactorhub.tool.parser.impl.java

import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElement
import jp.ac.titech.cs.se.refactorhub.tool.parser.CodeElementParser
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.CompilationUnit

class JavaParser : CodeElementParser {

    override fun parse(text: String, path: String): List<CodeElement> {
        val parser = ASTParser.newParser(AST.JLS14)
        parser.setSource(text.toCharArray())
        val unit = parser.createAST(NullProgressMonitor()) as CompilationUnit
        val visitor = CodeElementVisitor(unit, path)
        unit.accept(visitor)
        return visitor.elements
    }
}
