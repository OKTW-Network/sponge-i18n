package one.oktw.i18n.command

import one.oktw.i18n.api.Registry
import one.oktw.i18n.test.TestTranslationProvider
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

class TestI18nDebug: CommandExecutor {
    companion object {
        val spec: CommandSpec = CommandSpec.builder()
                .permission("i18n.command.debug")
                .executor(TestI18nDebug())
                .description(Text.of("load the debug language translation"))
                .build()
    }

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        Registry.instance.register(TestTranslationProvider.instance)

        return CommandResult.success()
    }
}