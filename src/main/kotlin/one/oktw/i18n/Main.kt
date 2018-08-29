package one.oktw.i18n

import one.oktw.i18n.api.Registry
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.play.client.CPacketCreativeInventoryAction
import net.minecraft.network.play.server.SPacketSetSlot
import net.minecraft.network.play.server.SPacketWindowItems
import net.minecraft.server.MinecraftServer
import net.minecraft.world.WorldServer
import one.oktw.i18n.command.TestI18nDebug
import one.oktw.i18n.command.TestItem
import one.oktw.i18n.network.UserListener
import one.oktw.i18n.test.TestTranslationProvider
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent
import org.spongepowered.api.event.game.state.GamePreInitializationEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.Text
import javax.inject.Inject

@Plugin(
        id = "i18n",
        name = "i18n",
        version = "1.0-Snapshot",
        dependencies = [Dependency(id = "packetgate", version = "0.1.2")]
)
class Main {
    companion object {
        const val IDENTIFIER = "§]"
    }

    @Inject
    lateinit var plugin: PluginContainer

    @Listener
    fun onPreInit(event: GamePreInitializationEvent) {
        Sponge.getCommandManager().register(plugin, TestI18nDebug.spec, "i18n-debug")
        Sponge.getCommandManager().register(plugin, TestItem.spec, "i18n-test")
    }

    @Listener
    fun onUserJoin(event: ClientConnectionEvent.Join) {
        val packetGate = Sponge.getServiceManager().provide(PacketGate::class.java).get()
        val packetConnection = packetGate.connectionByPlayer(event.targetEntity).get()

        packetGate.registerListener(
                UserListener(event.targetEntity, Registry.instance),
                PacketListener.ListenerPriority.EARLY,
                packetConnection,
                SPacketSetSlot::class.java,
                SPacketWindowItems::class.java,
                CPacketCreativeInventoryAction::class.java
        )
    }

    @Listener
    fun onPlayerLanguage(event: PlayerChangeClientSettingsEvent) {
        val player = event.targetEntity
        val language = event.locale.toLanguageTag()
        Registry.instance.setLanguage(player, language)

        player.sendMessage(Text.of("you current language is ${event.locale.toLanguageTag()}"))

        (Sponge.getServer() as MinecraftServer).playerList.syncPlayerInventory(player as EntityPlayerMP)
    }
}