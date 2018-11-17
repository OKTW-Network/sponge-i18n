package one.oktw.i18n

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.play.client.CPacketCreativeInventoryAction
import net.minecraft.network.play.server.SPacketEntityMetadata
import net.minecraft.network.play.server.SPacketOpenWindow
import net.minecraft.network.play.server.SPacketSetSlot
import net.minecraft.network.play.server.SPacketWindowItems
import net.minecraft.server.MinecraftServer
import one.oktw.i18n.api.I18n
import one.oktw.i18n.api.service.TranslationService
import one.oktw.i18n.command.TestItem
import one.oktw.i18n.impl.I18nImpl
import one.oktw.i18n.network.UserListener
import one.oktw.i18n.test.TestTranslationProvider
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent
import org.spongepowered.api.event.game.state.GamePreInitializationEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.api.world.World
import javax.inject.Inject

@Plugin(
        id = "i18n",
        name = "i18n",
        version = "0.1.4.1",
        dependencies = [Dependency(id = "packetgate", version = "0.1.2")]
)
class Main {
    companion object {
        const val IDENTIFIER = "§]"
        lateinit var main: Main
    }

    @Inject
    lateinit var plugin: PluginContainer

    lateinit var languageService: TranslationService

    @Inject
    lateinit var logger: Logger

    init {
        main = this
    }

    @Suppress("UNUSED_PARAMETER")
    @Listener(order = Order.EARLY)
    fun onPreInit(event: GamePreInitializationEvent) {
        Sponge.getServiceManager().setProvider(plugin, I18n::class.java, I18nImpl)
        languageService = Sponge.getServiceManager().provide(I18n::class.java).get().register("i18n", TestTranslationProvider.instance, false)
        Sponge.getCommandManager().register(plugin, TestItem.spec, "i18n-test")
    }

    @Listener
    fun onUserJoin(event: ClientConnectionEvent.Join) {
        val packetGate = Sponge.getServiceManager().provide(PacketGate::class.java).get()
        val packetConnection = packetGate.connectionByPlayer(event.targetEntity).get()

        packetGate.registerListener(
                UserListener(event.targetEntity, I18nImpl.registry),
                PacketListener.ListenerPriority.EARLY,
                packetConnection,
                SPacketSetSlot::class.java,
                SPacketWindowItems::class.java,
                SPacketOpenWindow::class.java,
                SPacketEntityMetadata::class.java,
                CPacketCreativeInventoryAction::class.java
        )
    }


    private fun rewriteItem(item: ItemStack, player: Player): ItemStack {
        val language = I18nImpl.registry.getLanguage(player)

        val translated = ArrayList<Pair<Int, String>>()

        val newItem = item.copy()

        newItem.getSubCompound("display")?.let { nbt ->
            if (nbt.hasKey("Name", 8)) {
                val originalName = nbt.getString("Name")
                if (originalName.startsWith(IDENTIFIER)) {
                    try {
                        val json = originalName.drop(IDENTIFIER.length)
                        val text = TextSerializers.JSON.deserialize(json)
                        val result = I18nImpl.getLegacySerializer(language).serialize(text)

                        nbt.setString("Name", result)
                        translated.add(Pair(-1, json))
                    } catch (err: Throwable) {
                        Main.main.logger.error(err.localizedMessage)
                        Main.main.logger.error(err.stackTrace.joinToString(""))
                    }
                }
            }

            if (nbt.getTagId("Lore") == 9.toByte()) {
                val list = nbt.getTagList("Lore", 8)

                for (i in 0 until list.tagCount()) {
                    val originalName = list.getStringTagAt(i)

                    if (originalName.startsWith(IDENTIFIER)) {
                        try {
                            val json = originalName.drop(IDENTIFIER.length)
                            val text = TextSerializers.JSON.deserialize(json)
                            val result = I18nImpl.getLegacySerializer(language).serialize(text)
                            list.set(i, NBTTagString(result))

                            translated.add(Pair(i, json))
                        } catch (err: Throwable) {
                            Main.main.logger.error(err.localizedMessage)
                            Main.main.logger.error(err.stackTrace.joinToString(""))
                        }
                    }
                }
            }
        }

        if (translated.size > 0) {
            val translatedList = NBTTagList()
            val i18n = newItem.getOrCreateSubCompound("i18n")
            i18n.setTag("list", translatedList)

            translated.forEach { (index, json) ->
                val obj = JsonObject()
                obj.addProperty("index", index)
                obj.add("translation", JsonPrimitive(json))

                translatedList.appendTag(NBTTagString(obj.toString()))
            }
        }

        return newItem
    }


    @Listener
    fun onPlayerLanguage(event: PlayerChangeClientSettingsEvent) {
        val packetGate = Sponge.getServiceManager().provide(PacketGate::class.java).get()
        val packetConnection = packetGate.connectionByPlayer(event.targetEntity).orElse(null) ?: return

        val player = event.targetEntity
        I18nImpl.registry.setLanguage(player, event.locale)

        (Sponge.getServer() as MinecraftServer).playerList.syncPlayerInventory(player as EntityPlayerMP)

        (player.world as World).entities.forEach {
            if (it.type != EntityTypes.ITEM_FRAME) {
                return@forEach
            }

            logger.info("resynchronizing $it")

            val native = it as Entity

            val packet = SPacketEntityMetadata()

            packet.entityId = native.entityId
            val entries = native.dataManager.all ?: return@forEach

            packet.dataManagerEntries = entries.filter { entry ->
                entry.key.serializer == DataSerializers.ITEM_STACK
            }.map { entry ->
                if (entry.key.serializer == DataSerializers.ITEM_STACK) {
                    val itemStack = entry.value as? ItemStack ?: return@map entry

                    val clonedEntry = entry.copy()
                    clonedEntry.value = rewriteItem(itemStack, player)
                    clonedEntry
                } else {
                    entry
                }
            }

            packetConnection.sendPacket(packet)
        }
    }
}