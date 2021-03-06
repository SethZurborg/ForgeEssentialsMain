package com.forgeessentials.core.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.selections.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class RespawnHandler {

    public RespawnHandler()
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    public static WarpPoint getPlayerSpawn(EntityPlayerMP player, WorldPoint location)
    {
        UserIdent ident = new UserIdent(player);
        if (location == null)
            location = new WorldPoint(player);
        String spawnProperty = APIRegistry.perms.getPermission(ident, location, null, APIRegistry.perms.getPlayerGroups(ident), FEPermissions.SPAWN, true);
        WorldPoint point = null;
        if (spawnProperty == null)
            return null;
        if (spawnProperty.equalsIgnoreCase("bed"))
        {
            if (player.getBedLocation() != null)
            {
                ChunkCoordinates spawn = player.getBedLocation();
                EntityPlayer.verifyRespawnCoordinates(player.worldObj, spawn, true);
                point = new WorldPoint(player.dimension, spawn.posX, spawn.posY, spawn.posZ);
            }
        }
        else
        {
            point = WorldPoint.fromString(spawnProperty);
        }
        if (point == null)
            return null;
        return new WarpPoint(point, player.cameraYaw, player.cameraPitch);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerDeath(LivingDeathEvent e)
    {
        if (e.entityLiving instanceof EntityPlayer)
        {
            EntityPlayerMP player = (EntityPlayerMP) e.entityLiving;
            PlayerInfo.getPlayerInfo(player.getPersistentID()).setLastDeathLocation(new WarpPoint(player));
        }
    }
    
    @SubscribeEvent
    public void doRespawn(PlayerEvent.PlayerRespawnEvent e)
    {
        WarpPoint lastDeathLocation = PlayerInfo.getPlayerInfo(e.player.getPersistentID()).getLastDeathLocation();
        if (lastDeathLocation != null)
        {
            WarpPoint p = getPlayerSpawn((EntityPlayerMP) e.player, lastDeathLocation);
            if (p != null)
            {
                FunctionHelper.teleportPlayer((EntityPlayerMP) e.player, p);
                e.player.posX = p.xd;
                e.player.posY = p.yd;
                e.player.posZ = p.zd;
            }
        }
    }

}
