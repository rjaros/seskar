package seskar.compiler.value.backend

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.util.isTopLevelDeclaration
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import seskar.compiler.common.backend.JsName
import seskar.compiler.common.backend.irReturn
import seskar.compiler.common.backend.isReallyExternal

internal class ValueTransformer(
    private val context: IrPluginContext,
) : IrElementTransformerVoid() {
    override fun visitClass(
        declaration: IrClass,
    ): IrStatement {
        if (!isReallyExternal(declaration))
            return declaration

        val value = declaration.value()

        if (value != null && declaration.isTopLevelDeclaration && declaration.kind == ClassKind.OBJECT) {
            declaration.annotations += JsName(context, value.toJsName())
            return declaration
        }

        return super.visitClass(declaration)
    }

    override fun visitProperty(
        declaration: IrProperty,
    ): IrStatement {
        val value = declaration.value()
            ?: return declaration

        if (declaration.isTopLevelDeclaration) {
            declaration.annotations += JsName(context, value.toJsName())
        } else {
            addPropertyGetter(declaration, value)
        }

        return declaration
    }

    override fun visitFunction(
        declaration: IrFunction,
    ): IrStatement {
        val value = declaration.value()
            ?: return declaration

        addFunctionBody(declaration, value)

        return declaration
    }

    private fun addPropertyGetter(
        declaration: IrProperty,
        value: Value,
    ) {
        val getter = declaration.getter
            ?: error("No default getter!")

        getter.isInline = true
        getter.body = context.irFactory.createBlockBody(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            statements = listOf(
                irReturn(
                    type = context.irBuiltIns.nothingNType,
                    returnTargetSymbol = getter.symbol,
                    value = valueConstant(declaration, value),
                )
            )
        )
    }

    private fun addFunctionBody(
        declaration: IrFunction,
        value: Value,
    ) {
        declaration.isInline = true
        declaration.isExternal = false
        if (declaration is IrOverridableMember) {
            declaration.modality = Modality.FINAL
        }
        declaration.body = context.irFactory.createBlockBody(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            statements = listOf(
                irReturn(
                    type = declaration.returnType,
                    returnTargetSymbol = declaration.symbol,
                    value = valueConstant(declaration, value),
                )
            )
        )
    }

    private fun valueConstant(
        declaration: IrDeclarationWithName,
        value: Value,
    ): IrExpression {
        return when (value) {
            is IntValue ->
                IrConstImpl.int(
                    startOffset = declaration.startOffset,
                    endOffset = declaration.endOffset,
                    type = context.irBuiltIns.intType,
                    value = value.value,
                )

            is StringValue ->
                IrConstImpl.string(
                    startOffset = declaration.startOffset,
                    endOffset = declaration.endOffset,
                    type = context.irBuiltIns.stringType,
                    value = value.value,
                )
        }
    }
}
