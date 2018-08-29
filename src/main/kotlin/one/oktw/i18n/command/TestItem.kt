package one.oktw.i18n.command

import one.oktw.i18n.api.Translation
import one.oktw.i18n.translation.Helper
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import java.util.Arrays.asList

class TestItem: CommandExecutor {
    companion object {
        val spec: CommandSpec = CommandSpec.builder()
                .permission("i18n.command.test")
                .executor(TestItem())
                .description(Text.of("give i18n test translated item"))
                .build()
    }

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) return CommandResult.empty()

        val item = ItemStack.of(ItemTypes.IRON_SWORD, 1).apply {
            offer(
                    Keys.DISPLAY_NAME, Text.of(Helper.placeholder(
                    "tooltip.item_owned",
                    src.name,
                    Translation("item.sword")))
            )

            offer(Keys.ITEM_LORE, asList<Text>(
                    Text.of(Helper.placeholder("tooltip.description"))
            ))
        }

        src.inventory.offer(item)

        return CommandResult.success()
    }
}