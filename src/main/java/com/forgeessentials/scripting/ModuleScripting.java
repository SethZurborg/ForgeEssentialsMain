package com.forgeessentials.scripting;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

@FEModule(name = "scripting", parentMod = ForgeEssentials.class, isCore = false)
public class ModuleScripting {

    @FEModule.ModuleDir
    public static File moduleDir = new File(ForgeEssentials.FEDIR, "scripting/");

    static File loginplayer = new File(moduleDir, "login/player/");
    static File logingroup = new File(moduleDir, "login/group/");
    static File respawngroup = new File(moduleDir, "respawn/group/");
    static File respawnplayer = new File(moduleDir, "respawn/player/");

    public static void startup()
    {
        try
        {
            moduleDir.mkdirs();
            for (EventType e : EventType.values())
            {
                e.mkdirs();
            }

        }
        catch (Exception e)
        {
            OutputHandler.felog.warning("Could not setup scripting folders - you might have to do it yourself.");
        }
    }

    @SubscribeEvent
    public void preInit(FEModulePreInitEvent e)
    {
        OutputHandler.felog.info("Scripts are being read from " + moduleDir.getAbsolutePath());
        startup();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        FunctionHelper.registerServerCommand(new CommandScript());
        FunctionHelper.registerServerCommand(new TimedTaskManager());
    }
}
