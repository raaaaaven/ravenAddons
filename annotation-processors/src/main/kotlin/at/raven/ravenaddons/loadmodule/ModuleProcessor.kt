package at.raven.ravenaddons.loadmodule

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import java.io.OutputStreamWriter

class ModuleProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(LoadModule::class.qualifiedName!!).toList()
        val validSymbols = symbols.mapNotNull { validateSymbol(it) }
        if (validSymbols.isNotEmpty()) {
            generateFile(validSymbols)
        }
        return emptyList()
    }
    private fun validateSymbol(symbol: KSAnnotated): KSClassDeclaration? {
        if (!symbol.validate()) {
            logger.warn("Symbol is not valid: $symbol")
            return null
        }
        if (symbol !is KSClassDeclaration) {
            logger.error("@LoadModule is only valid on class declarations", symbol)
            return null
        }
        if (symbol.classKind != ClassKind.OBJECT) {
            logger.error("@LoadModule is only valid on kotlin objects", symbol)
            return null
        }
        return symbol
    }
    private fun generateFile(symbols: List<KSClassDeclaration>) {
        val dependencies = symbols.mapNotNull { it.containingFile }.toTypedArray()
        val deps = Dependencies(true, *dependencies)
        val file = codeGenerator.createNewFile(deps, "at.raven.ravenaddons.loadmodule", "LoadedModules")
        OutputStreamWriter(file).use {
            it.write("package at.raven.ravenaddons.loadmodule\n\n")
            it.write("object LoadedModules {\n")
            it.write("    val modules: List<Any> = listOf(\n")
            symbols.forEach { symbol ->
                it.write("        ${symbol.qualifiedName!!.asString()},\n")
            }
            it.write("    )\n")
            it.write("}\n")
        }
        logger.warn("Generated LoadedModules file with ${symbols.size} modules")
    }
}