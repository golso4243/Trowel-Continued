package com.swornhero.trowelcontinued

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("MemberVisibilityCanBePrivate")
object TrowelMod : ModInitializer {
    const val MODID = "trowel_continued"

    val log: Logger = LoggerFactory.getLogger(MODID)

    private val TROWEL_ID: Identifier =
        Identifier.fromNamespaceAndPath(MODID, "trowel")

    private val TROWEL_KEY: ResourceKey<Item> =
        ResourceKey.create(Registries.ITEM, TROWEL_ID)

    val TROWEL: Item = Registry.register(
        BuiltInRegistries.ITEM,
        TROWEL_KEY,
        Trowel(
            Item.Properties()
                .stacksTo(1)
                .setId(TROWEL_KEY)
        )
    )

    override fun onInitialize() {
        log.info("Seizing the means of block placement!")

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register { entries ->
                entries.insertAfter(Items.SHEARS, TROWEL)
            }
    }
}