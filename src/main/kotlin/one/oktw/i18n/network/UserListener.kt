package one.oktw.i18n.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import one.oktw.i18n.Main.Companion.IDENTIFIER
import one.oktw.i18n.api.Registry
import com.google.gson.JsonParser
import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.play.client.CPacketCreativeInventoryAction
import net.minecraft.network.play.server.SPacketSetSlot
import net.minecraft.network.play.server.SPacketWindowItems
import org.spongepowered.api.entity.living.player.Player
import one.oktw.i18n.translation.Helper

@Suppress("unused")
class UserListener(private val player: Player, private val registry: Registry) : PacketListenerAdapter() {
    override fun onPacketWrite(packetEvent: PacketEvent, connection: PacketConnection) {
        packetEvent.packet.let { it as? SPacketSetSlot }?.let { handle(it, packetEvent, connection) }
        packetEvent.packet.let { it as? SPacketWindowItems }?.let { handle(it, packetEvent, connection) }
    }

    override fun onPacketRead(packetEvent: PacketEvent, connection: PacketConnection) {
        packetEvent.packet.let { it as? CPacketCreativeInventoryAction }?.let { handle(it, packetEvent, connection) }
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
        val displayCompound = item.getSubCompound("display")?: return
        val parser = JsonParser()

        for (i in 0 until nbtTagList.tagCount()) {
            try {
                val obj = parser.parse(nbtTagList.getStringTagAt(i)).asJsonObject
                val index = obj.getAsJsonPrimitive("index").asInt
                val tree = obj.getAsJsonObject("translation")

                when (index) {
                    -1 -> {
                        displayCompound.setString("Name", IDENTIFIER + tree.toString())
                    }
                    else -> {
                        displayCompound.getTagList("Lore", 8).set(index, NBTTagString(IDENTIFIER + tree.toString()))
                    }
                }
            } catch (err: Throwable) {
            }
        }
    }

    private fun rewriteItem(item: ItemStack): ItemStack {
        val language = Registry.instance.getLanguage(player)

        val translated = ArrayList<Pair<Int, JsonElement>>()

        val newItem = item.copy()

        newItem.getSubCompound("display")?.let { nbt ->
            if (nbt.hasKey("Name", 8)) {
                val originalName = nbt.getString("Name")
                if (originalName.startsWith(IDENTIFIER)) {
                    try {
                        val parser = JsonParser()
                        val tree = parser.parse(originalName.drop(IDENTIFIER.length))
                        val result = Helper.translate(language, tree) ?: return@let

                        nbt.setString("Name", result)
                        translated.add(Pair(-1, tree))
                    } catch (err: Throwable) {
                    }
                }
            }

            if (nbt.getTagId("Lore") == 9.toByte()) {
                val list = nbt.getTagList("Lore", 8)

                for (i in 0 until list.tagCount()) {
                    val originalName = list.getStringTagAt(i)

                    if (originalName.startsWith(IDENTIFIER)) {
                        try {
                            val parser = JsonParser()
                            val tree = parser.parse(originalName.drop(IDENTIFIER.length))
                            val result = Helper.translate(language, tree) ?: return@let
                            list.set(i, NBTTagString(result))

                            translated.add(Pair(i, tree))
                        } catch (err: Throwable) {
                        }
                    }
                }
            }
        }

        if (translated.size > 0) {
            val translatedList = NBTTagList()
            val i18n = item.getOrCreateSubCompound("i18n")
            i18n.setTag("list", i18n)

            translated.forEach { (index, json) ->
                val obj = JsonObject()
                obj.addProperty("index", index)
                obj.add("translation", json)

                translatedList.appendTag(NBTTagString(obj.toString()))
            }
        }

        return newItem
    }
}