package net.sdm.recipemachinestage.client;

import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.gamestages.event.StagesSyncedEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.sdm.recipemachinestage.RecipeMachineStage;
import net.sdm.recipemachinestage.compat.jei.JEIPlugin;

@Mod.EventBusSubscriber(modid = RecipeMachineStage.MODID, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onGamestageSync(StagesSyncedEvent event) {
        if(ModList.get().isLoaded("jei")) {
            Minecraft.getInstance().execute(() -> {
                JEIPlugin.sync(event.getData());
            });
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void recipes(RecipesUpdatedEvent event) {
        if(ModList.get().isLoaded("jei")) {
            Minecraft.getInstance().execute(() -> {
                IStageData data = GameStageSaveHandler.getClientData();
                if(data == null) {
                    return;
                }
                JEIPlugin.sync(data);
            });
        }

    }
}