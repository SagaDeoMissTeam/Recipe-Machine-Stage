package net.sdm.recipemachinestage.mixin.integration.create.create_craft_addition;

import com.mrh0.createaddition.blocks.rolling_mill.RollingMillBlockEntity;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.sdm.recipemachinestage.SupportBlockData;
import net.sdm.recipemachinestage.capability.IOwnerBlock;
import net.sdm.recipemachinestage.stage.StageContainer;
import net.sdm.recipemachinestage.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = RollingMillBlockEntity.class, remap = false)
public class RollingMillBlockEntityMixin {

    private RollingMillBlockEntity thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "find", at = @At("RETURN"), cancellable = true)
    private void sdm$find(RecipeWrapper wrapper, Level world, CallbackInfoReturnable<Optional<RollingRecipe>> cir) {
        Optional<RollingRecipe> optional = cir.getReturnValue();

        if(optional.isPresent() ) {
            RollingRecipe recipe = optional.get();

//            if(!StageContainer.hasRecipes(recipe.getType())) return;

            Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
            if (d1.isPresent() && thisEntity.getLevel().getServer()!= null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            cir.setReturnValue(Optional.empty());
                        }
                    }
                }
            }
        }

    }
}
