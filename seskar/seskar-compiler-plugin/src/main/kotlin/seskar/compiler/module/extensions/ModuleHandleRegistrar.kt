package seskar.compiler.module.extensions

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.config.CompilerConfiguration
import seskar.compiler.common.extensions.JsCompilerPluginRegistrar

class ModuleHandleRegistrar : JsCompilerPluginRegistrar() {
    override fun ExtensionStorage.registerExtensions(
        configuration: CompilerConfiguration,
    ) {
        IrGenerationExtension.registerExtension(ModuleHandleExtension())
    }
}
