package me.spartacus04.jext.integrations

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.flags.StateFlag
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.Bukkit


internal class WorldGuardPermissionIntegration : PermissionIntegration {
    override val id = "worldguard"

    init {
        Flags.CHEST_ACCESS
    }

    override fun hasJukeboxAccess(player: Player, block: Block): Boolean = canInteract(player, block, Flags.USE)

    override fun hasJukeboxGuiAccess(player: Player, block: Block): Boolean = canInteract(player, block, Flags.CHEST_ACCESS, Flags.USE)

    private fun canInteract(player: Player, block: Block, vararg flags: StateFlag): Boolean {
		val wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
		val wgLocation = BukkitAdapter.adapt(block.location)
		if(WorldGuard.getInstance().platform.sessionManager.hasBypass(wgPlayer, wgPlayer.world)) return true
		val regionContainer = WorldGuard.getInstance().platform.regionContainer
		val regions = regionContainer.createQuery().getApplicableRegions(wgLocation)
		if(regions.size() == 0) return true  // no regions, allow it
		val containerQuery = regionContainer.createQuery()
		return flags.all {
			containerQuery.testState(wgLocation, wgPlayer, it) != false
		}
	}
}
