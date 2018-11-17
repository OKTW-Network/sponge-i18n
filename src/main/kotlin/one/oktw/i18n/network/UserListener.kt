package one.oktw.i18n.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.play.client.CPacketCreativeInventoryAction
import net.minecraft.network.play.server.SPacketEntityMetadata
import net.minecraft.network.play.server.SPacketOpenWindow
import net.minecraft.network.play.server.SPacketSetSlot
import net.minecraft.network.play.server.SPacketWindowItems
import net.minecraft.util.text.TextComponentString
import one.oktw.i18n.Main
import one.oktw.i18n.Main.Companion.IDENTIFIER
import one.oktw.i18n.api.Registry
import one.oktw.i18n.impl.I18nImpl
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.serializer.TextSerializers

@Suppress("unused")
class UserListener(private val player: Player, private val registry: Registry) : PacketListenerAdapter() {
    override fun onPacketWrite(packetEvent: PacketEvent, connection: PacketConnection) {
        packetEvent.packet.let { it as? SPacketSetSlot }?.let { handle(it, packetEvent, connection) }
        packetEvent.packet.let { it as? SPacketWindowItems }?.let { handle(it, packetEvent, connection) }
        packetEvent.packet.let { it as? SPacketOpenWindow }?.let { handle(it, packetEvent, connection) }
        packetEvent.packet.let { it as? SPacketEntityMetadata }?.let { handle(it, packetEvent, connection) }
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
        val displayCompound = item.getSubCompound("display") ?: return
        val parser = JsonParser()

        for (i in 0 until nbtTagList.tagCount()) {
            try {
                val obj = parser.parse(nbtTagList.getStringTagAt(i)).asJsonObject
                val index = obj.getAsJsonPrimitive("index").asInt
                val json = obj.getAsJsonPrimitive("translation").asString

                when (index) {
                    -1 -> {
                        displayCompound.setString("Name", IDENTIFIER + json)
                    }
                    else -> {
                        displayCompound.getTagList("Lore", 8).set(index, NBTTagString(IDENTIFIER + json))
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
        cloned.dataManagerEntries = packet.dataManagerEntries.map {entry->
            if (entry.key.serializer == DataSerializers.ITEM_STACK) {
                val itemStack = entry.value as? ItemStack?: return@map entry

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

    private fun rewriteItem(item: ItemStack): ItemStack {
        val language = registry.getLanguage(player)

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
}