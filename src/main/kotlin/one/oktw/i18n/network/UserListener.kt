package one.oktw.i18n.network

import com.google.gson.JsonParser
import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection
import net.minecraft.item.ItemStack
import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.play.client.CPacketCreativeInventoryAction
import net.minecraft.network.play.server.*
import net.minecraft.util.text.TextComponentString
import one.oktw.i18n.Main
import one.oktw.i18n.Main.Companion.IDENTIFIER
import one.oktw.i18n.api.Registry
import one.oktw.i18n.impl.I18nImpl
import one.oktw.i18n.text.Helper
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.serializer.TextSerializers

@Suppress("unused")
class UserListener(private val player: Player, private val registry: Registry) : PacketListenerAdapter() {
    override fun onPacketWrite(packetEvent: PacketEvent, connection: PacketConnection) {
        packetEvent.packet.let { it as? SPacketSetSlot }?.let { handle(it, packetEvent, connection) }
        packetEvent.packet.let { it as? SPacketWindowItems }?.let { handle(it, packetEvent, connection) }
        packetEvent.packet.let { it as? SPacketOpenWindow }?.let { handle(it, packetEvent, connection) }
        packetEvent.packet.let { it as? SPacketEntityMetadata }?.let { handle(it, packetEvent, connection) }
        packetEvent.packet.let { it as? SPacketChat }?.let { handle(it, packetEvent, connection) }
    }

    override fun onPacketRead(packetEvent: PacketEvent, connection: PacketConnection) {
        packetEvent.packet.let { it as? CPacketCreativeInventoryAction }?.let { handle(it, packetEvent, connection) }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handle(packet: SPacketOpenWindow, packetEvent: PacketEvent, connection: PacketConnection) {
        val language = registry.getLanguage(player)

        val title = packet.windowTitle.formattedText

        if (title.startsWith(IDENTIFIER)) {
            try {
                val translated = I18nImpl.getLegacySerializer(language).serialize(
                        TextSerializers.JSON.deserialize(title.drop(IDENTIFIER.length))
                )

                packet.windowTitle = TextComponentString(translated)
            } catch (err: Throwable) {
                Main.main.logger.error(err.localizedMessage)
                Main.main.logger.error(err.stackTrace.joinToString(""))
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handle(packet: SPacketSetSlot, packetEvent: PacketEvent, connection: PacketConnection) {
        packet.item = rewriteItem(packet.item)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handle(packet: SPacketWindowItems, packetEvent: PacketEvent, connection: PacketConnection) {
        packet.itemStacks = packet.itemStacks.map { rewriteItem(it) }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handle(packet: CPacketCreativeInventoryAction, packetEvent: PacketEvent, connection: PacketConnection) {
        val item = packet.stack

        val translationNBT = item.getSubCompound("i18n") ?: return
        item.removeSubCompound("i18n")

        val nbtTagList = translationNBT.getTagList("list", 8)
        val displayCompound = item.getSubCompound("display")
        val parser = JsonParser()

        for (i in 0 until nbtTagList.tagCount()) {
            try {
                val obj = parser.parse(nbtTagList.getStringTagAt(i)).asJsonObject
                val index = obj.getAsJsonPrimitive("index").asInt
                val json = obj.getAsJsonPrimitive("translation").asString

                when (index) {
                    -1 -> {
                        displayCompound?.setString("Name", IDENTIFIER + json)
                    }
                    -2 -> {
                        try {
                            JsonToNBT.getTagFromJson(json).let {
                                item.setTagInfo("BlockEntityTag", it)
                            }
                        } catch (err: Throwable) {
                            Main.main.logger.error(err.localizedMessage)
                            Main.main.logger.error(err.stackTrace.joinToString(""))
                        }
                    }
                    else -> {
                        displayCompound?.getTagList("Lore", 8)?.set(index, NBTTagString(IDENTIFIER + json))
                    }
                }
            } catch (err: Throwable) {
                Main.main.logger.error(err.localizedMessage)
                Main.main.logger.error(err.stackTrace.joinToString(""))
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handle(packet: SPacketEntityMetadata, packetEvent: PacketEvent, connection: PacketConnection) {
        var tained = false
        val cloned = SPacketEntityMetadata()
        cloned.entityId = packet.entityId
        cloned.dataManagerEntries = packet.dataManagerEntries.map { entry ->
            if (entry.key.serializer == DataSerializers.ITEM_STACK) {
                val itemStack = entry.value as? ItemStack ?: return@map entry
                val clonedEntry = entry.copy()
                tained = true
                clonedEntry.value = rewriteItem(itemStack)
                clonedEntry
            } else {
                entry
            }
        }

        if (tained) {
            packetEvent.packet = cloned
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handle(packet: SPacketChat, packetEvent: PacketEvent, connection: PacketConnection) {
        val newComponent = Helper.fixTextComponentServerSide(packet.chatComponent, I18nImpl.registry.getLanguage(player))
        val newPacket = SPacketChat(newComponent, packet.type)
        packetEvent.packet = newPacket
    }

    private fun rewriteItem(item: ItemStack): ItemStack {
        return Helper.rewriteItem(item, player)
    }
}