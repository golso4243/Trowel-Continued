package com.swornhero.trowelcontinued

import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.phys.BlockHitResult

class Trowel(properties: Properties) : Item(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level

        if (level.isClientSide) {
            return InteractionResult.PASS
        }

        val player = context.player ?: return InteractionResult.PASS
        val inventory = player.inventory

        val placeable = (0..8).mapNotNull { slot ->
            val stack = inventory.getItem(slot)

            if (isPlaceable(stack)) {
                slot to stack
            } else {
                null
            }
        }

        if (placeable.isEmpty()) {
            return InteractionResult.PASS
        }

        val originalSlot = inventory.selectedSlot

        return placeRandomBlock(
            placeable = placeable,
            inventory = inventory,
            player = player,
            context = context,
            originalSlot = originalSlot
        )
    }

    private fun placeRandomBlock(
        placeable: List<Pair<Int, ItemStack>>,
        inventory: Inventory,
        player: Player,
        context: UseOnContext,
        originalSlot: Int
    ): InteractionResult {
        if (placeable.isEmpty()) {
            inventory.selectedSlot = originalSlot
            return InteractionResult.FAIL
        }

        val selected = placeable.random()
        val slot = selected.first
        val stack = selected.second

        inventory.selectedSlot = slot

        val blockItem = stack.item as BlockItem
        val hitResult = BlockHitResult(
            context.clickLocation,
            context.clickedFace,
            context.clickedPos,
            context.isInside
        )

        val placementContext = BlockPlaceContext(
            player,
            context.hand,
            stack,
            hitResult
        )

        val result = blockItem.place(placementContext)

        inventory.selectedSlot = originalSlot

        if (result.consumesAction()) {
            playPlacementSound(placementContext, blockItem)
            return result
        }

        return placeRandomBlock(
            placeable = placeable.filterNot { it.first == slot },
            inventory = inventory,
            player = player,
            context = context,
            originalSlot = originalSlot
        )
    }

    private fun playPlacementSound(
        context: BlockPlaceContext,
        blockItem: BlockItem
    ) {
        val level = context.level
        val blockState = blockItem.block.defaultBlockState()
        val soundType = blockState.soundType

        level.playSound(
            null,
            context.clickedPos,
            soundType.placeSound,
            SoundSource.BLOCKS,
            (soundType.volume + 1.0f) / 2.0f,
            soundType.pitch * 0.8f
        )
    }

    private fun isPlaceable(stack: ItemStack): Boolean {
        return !stack.isEmpty && stack.item is BlockItem
    }
}