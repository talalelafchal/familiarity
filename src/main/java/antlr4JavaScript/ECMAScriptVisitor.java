package antlr4JavaScript;// Generated from ECMAScript.g4 by ANTLR 4.3
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ECMAScriptParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ECMAScriptVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code PropertyExpressionAssignment}
	 * labeled alternative in {@link ECMAScriptParser#propertyAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyExpressionAssignment(@NotNull ECMAScriptParser.PropertyExpressionAssignmentContext ctx);

	/**
	 * Visit a parse tree produced by the {@code InExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInExpression(@NotNull ECMAScriptParser.InExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#eos}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEos(@NotNull ECMAScriptParser.EosContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#sourceElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSourceElements(@NotNull ECMAScriptParser.SourceElementsContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(@NotNull ECMAScriptParser.ProgramContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#argumentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentList(@NotNull ECMAScriptParser.ArgumentListContext ctx);

	/**
	 * Visit a parse tree produced by the {@code ArgumentsExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentsExpression(@NotNull ECMAScriptParser.ArgumentsExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#identifierName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierName(@NotNull ECMAScriptParser.IdentifierNameContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#initialiser}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitialiser(@NotNull ECMAScriptParser.InitialiserContext ctx);

	/**
	 * Visit a parse tree produced by the {@code TypeofExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeofExpression(@NotNull ECMAScriptParser.TypeofExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(@NotNull ECMAScriptParser.BlockContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#expressionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStatement(@NotNull ECMAScriptParser.ExpressionStatementContext ctx);

	/**
	 * Visit a parse tree produced by the {@code BitXOrExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitXOrExpression(@NotNull ECMAScriptParser.BitXOrExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code MultiplicativeExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(@NotNull ECMAScriptParser.MultiplicativeExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#numericLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericLiteral(@NotNull ECMAScriptParser.NumericLiteralContext ctx);

	/**
	 * Visit a parse tree produced by the {@code ForInStatement}
	 * labeled alternative in {@link ECMAScriptParser#iterationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInStatement(@NotNull ECMAScriptParser.ForInStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#emptyStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmptyStatement(@NotNull ECMAScriptParser.EmptyStatementContext ctx);

	/**
	 * Visit a parse tree produced by the {@code AdditiveExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(@NotNull ECMAScriptParser.AdditiveExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code RelationalExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(@NotNull ECMAScriptParser.RelationalExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#labelledStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabelledStatement(@NotNull ECMAScriptParser.LabelledStatementContext ctx);

	/**
	 * Visit a parse tree produced by the {@code NewExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewExpression(@NotNull ECMAScriptParser.NewExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#withStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithStatement(@NotNull ECMAScriptParser.WithStatementContext ctx);

	/**
	 * Visit a parse tree produced by the {@code BitAndExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitAndExpression(@NotNull ECMAScriptParser.BitAndExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code BitOrExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitOrExpression(@NotNull ECMAScriptParser.BitOrExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code VoidExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVoidExpression(@NotNull ECMAScriptParser.VoidExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code TernaryExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTernaryExpression(@NotNull ECMAScriptParser.TernaryExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code LogicalAndExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalAndExpression(@NotNull ECMAScriptParser.LogicalAndExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#arrayLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLiteral(@NotNull ECMAScriptParser.ArrayLiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#elision}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElision(@NotNull ECMAScriptParser.ElisionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code WhileStatement}
	 * labeled alternative in {@link ECMAScriptParser#iterationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(@NotNull ECMAScriptParser.WhileStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(@NotNull ECMAScriptParser.ReturnStatementContext ctx);

	/**
	 * Visit a parse tree produced by the {@code PreDecreaseExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreDecreaseExpression(@NotNull ECMAScriptParser.PreDecreaseExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#expressionSequence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionSequence(@NotNull ECMAScriptParser.ExpressionSequenceContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(@NotNull ECMAScriptParser.LiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#defaultClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefaultClause(@NotNull ECMAScriptParser.DefaultClauseContext ctx);

	/**
	 * Visit a parse tree produced by the {@code PostDecreaseExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostDecreaseExpression(@NotNull ECMAScriptParser.PostDecreaseExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code UnaryPlusExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryPlusExpression(@NotNull ECMAScriptParser.UnaryPlusExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code ForStatement}
	 * labeled alternative in {@link ECMAScriptParser#iterationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(@NotNull ECMAScriptParser.ForStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#caseBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseBlock(@NotNull ECMAScriptParser.CaseBlockContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#caseClauses}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseClauses(@NotNull ECMAScriptParser.CaseClausesContext ctx);

	/**
	 * Visit a parse tree produced by the {@code ParenthesizedExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesizedExpression(@NotNull ECMAScriptParser.ParenthesizedExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code PostIncrementExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostIncrementExpression(@NotNull ECMAScriptParser.PostIncrementExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#objectLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectLiteral(@NotNull ECMAScriptParser.ObjectLiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#throwStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThrowStatement(@NotNull ECMAScriptParser.ThrowStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(@NotNull ECMAScriptParser.VariableDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by the {@code IdentifierExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierExpression(@NotNull ECMAScriptParser.IdentifierExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#propertyName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyName(@NotNull ECMAScriptParser.PropertyNameContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#caseClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseClause(@NotNull ECMAScriptParser.CaseClauseContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#variableDeclarationList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclarationList(@NotNull ECMAScriptParser.VariableDeclarationListContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#setter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetter(@NotNull ECMAScriptParser.SetterContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#functionDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDeclaration(@NotNull ECMAScriptParser.FunctionDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#getter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetter(@NotNull ECMAScriptParser.GetterContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#assignmentOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentOperator(@NotNull ECMAScriptParser.AssignmentOperatorContext ctx);

	/**
	 * Visit a parse tree produced by the {@code ThisExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisExpression(@NotNull ECMAScriptParser.ThisExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#futureReservedWord}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFutureReservedWord(@NotNull ECMAScriptParser.FutureReservedWordContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#statementList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementList(@NotNull ECMAScriptParser.StatementListContext ctx);

	/**
	 * Visit a parse tree produced by the {@code PropertyGetter}
	 * labeled alternative in {@link ECMAScriptParser#propertyAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyGetter(@NotNull ECMAScriptParser.PropertyGetterContext ctx);

	/**
	 * Visit a parse tree produced by the {@code EqualityExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(@NotNull ECMAScriptParser.EqualityExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#keyword}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyword(@NotNull ECMAScriptParser.KeywordContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#elementList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementList(@NotNull ECMAScriptParser.ElementListContext ctx);

	/**
	 * Visit a parse tree produced by the {@code BitShiftExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitShiftExpression(@NotNull ECMAScriptParser.BitShiftExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code BitNotExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitNotExpression(@NotNull ECMAScriptParser.BitNotExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code PropertySetter}
	 * labeled alternative in {@link ECMAScriptParser#propertyAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertySetter(@NotNull ECMAScriptParser.PropertySetterContext ctx);

	/**
	 * Visit a parse tree produced by the {@code LiteralExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralExpression(@NotNull ECMAScriptParser.LiteralExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code ArrayLiteralExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLiteralExpression(@NotNull ECMAScriptParser.ArrayLiteralExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code MemberDotExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberDotExpression(@NotNull ECMAScriptParser.MemberDotExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code MemberIndexExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberIndexExpression(@NotNull ECMAScriptParser.MemberIndexExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#formalParameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameterList(@NotNull ECMAScriptParser.FormalParameterListContext ctx);

	/**
	 * Visit a parse tree produced by the {@code ForVarInStatement}
	 * labeled alternative in {@link ECMAScriptParser#iterationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForVarInStatement(@NotNull ECMAScriptParser.ForVarInStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#propertySetParameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertySetParameterList(@NotNull ECMAScriptParser.PropertySetParameterListContext ctx);

	/**
	 * Visit a parse tree produced by the {@code AssignmentOperatorExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentOperatorExpression(@NotNull ECMAScriptParser.AssignmentOperatorExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#tryStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTryStatement(@NotNull ECMAScriptParser.TryStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#debuggerStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDebuggerStatement(@NotNull ECMAScriptParser.DebuggerStatementContext ctx);

	/**
	 * Visit a parse tree produced by the {@code DoStatement}
	 * labeled alternative in {@link ECMAScriptParser#iterationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoStatement(@NotNull ECMAScriptParser.DoStatementContext ctx);

	/**
	 * Visit a parse tree produced by the {@code PreIncrementExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreIncrementExpression(@NotNull ECMAScriptParser.PreIncrementExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code ObjectLiteralExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectLiteralExpression(@NotNull ECMAScriptParser.ObjectLiteralExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code LogicalOrExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOrExpression(@NotNull ECMAScriptParser.LogicalOrExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code NotExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpression(@NotNull ECMAScriptParser.NotExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#switchStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchStatement(@NotNull ECMAScriptParser.SwitchStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#variableStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableStatement(@NotNull ECMAScriptParser.VariableStatementContext ctx);

	/**
	 * Visit a parse tree produced by the {@code FunctionExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionExpression(@NotNull ECMAScriptParser.FunctionExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code UnaryMinusExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryMinusExpression(@NotNull ECMAScriptParser.UnaryMinusExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code AssignmentExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentExpression(@NotNull ECMAScriptParser.AssignmentExpressionContext ctx);

	/**
	 * Visit a parse tree produced by the {@code InstanceofExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstanceofExpression(@NotNull ECMAScriptParser.InstanceofExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(@NotNull ECMAScriptParser.StatementContext ctx);

	/**
	 * Visit a parse tree produced by the {@code DeleteExpression}
	 * labeled alternative in {@link ECMAScriptParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeleteExpression(@NotNull ECMAScriptParser.DeleteExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#propertyNameAndValueList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyNameAndValueList(@NotNull ECMAScriptParser.PropertyNameAndValueListContext ctx);

	/**
	 * Visit a parse tree produced by the {@code ForVarStatement}
	 * labeled alternative in {@link ECMAScriptParser#iterationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForVarStatement(@NotNull ECMAScriptParser.ForVarStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#sourceElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSourceElement(@NotNull ECMAScriptParser.SourceElementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#breakStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(@NotNull ECMAScriptParser.BreakStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(@NotNull ECMAScriptParser.IfStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#reservedWord}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReservedWord(@NotNull ECMAScriptParser.ReservedWordContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#finallyProduction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFinallyProduction(@NotNull ECMAScriptParser.FinallyProductionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#catchProduction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCatchProduction(@NotNull ECMAScriptParser.CatchProductionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#continueStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStatement(@NotNull ECMAScriptParser.ContinueStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(@NotNull ECMAScriptParser.ArgumentsContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#functionBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionBody(@NotNull ECMAScriptParser.FunctionBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link ECMAScriptParser#eof}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEof(@NotNull ECMAScriptParser.EofContext ctx);
}