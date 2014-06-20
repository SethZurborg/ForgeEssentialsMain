package com.forgeessentials.util.events.modules;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.moduleLauncher.ModuleContainer;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FEModuleServerInitEvent extends FEModuleEvent {
    private static Map<String, RegGroup> permList = new HashMap<String, RegGroup>();
    private FMLServerStartingEvent event;

    public FEModuleServerInitEvent(ModuleContainer container, FMLServerStartingEvent event)
    {
        super(container);
        this.event = event;
    }

    @Override
    public FMLStateEvent getFMLEvent()
    {
        return event;
    }

    public MinecraftServer getServer()
    {
        return event.getServer();
    }

    public void registerServerCommand(ForgeEssentialsCommandBase command)
    {
        System.out.println(command.getClass().getCanonicalName());
        if (!(command.getCommandPerm() == null))
        {
            permList.put(command.getCommandPerm(), command.getReggroup());
        }
        event.registerServerCommand(command);
    }

    @PermRegister
    public void registerPermissions(IPermRegisterEvent e)
    {
        Iterator it = permList.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pairs = (Map.Entry) it.next();
            e.registerPermissionLevel((String) pairs.getKey(), (RegGroup) pairs.getValue());
            System.out.println((String) pairs.getKey());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
}
