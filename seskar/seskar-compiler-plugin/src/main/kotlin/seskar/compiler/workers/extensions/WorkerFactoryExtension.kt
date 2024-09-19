package seskar.compiler.workers.extensions

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.platform.isJs
import seskar.compiler.workers.backend.WorkerFactoryTransformer

internal class WorkerFactoryExtension : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        pluginContext.platform
            ?.takeIf { it.isJs() }
            ?: return

        moduleFragment.transformChildren(WorkerFactoryTransformer(pluginContext), null)
    }
}
