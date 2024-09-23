package net.sdm.recipemachinestage;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sdm.recipemachinestage.capability.IOwnerableSupport;
import net.sdm.recipemachinestage.stage.StageContainer;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RecipeMachineStage.MODID)
public class RecipeMachineStage {

    public static final String MODID = "recipemachinestage";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RecipeMachineStage() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);



        SupportBlockData.init();
    }

    @SubscribeEvent
    public void onAddReload(AddReloadListenerEvent event){
        event.addListener(StageContainer.INSTANCE);
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event){
        if(event.getEntity() instanceof Player player) {
            BlockPos pos = event.getPos();
            BlockEntity blockEntity = event.getLevel().getBlockEntity(pos);
            if(blockEntity instanceof IOwnerableSupport support) {
                support.recipe_machine_stage$setOwner(player.getGameProfile().getId());
            }
        }
    }
}
