package net.sdm.recipemachinestage.mixin.integration.natures_aura;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityOfferingTable;
import de.ellpeck.naturesaura.recipes.AltarRecipe;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import net.minecraft.world.item.ItemStack;
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

@Mixin(value = BlockEntityOfferingTable.class, remap = false)
public class BlockEntityOfferingTableMixin {

    private BlockEntityOfferingTable thisEntity = RecipeStagesUtil.cast(this);

    @Inject(method = "getRecipe", at = @At(value = "RETURN"), cancellable = true)
    public void sdm$getRecipe(ItemStack input, CallbackInfoReturnable<AltarRecipe> cir){
        if(StageContainer.INSTANCE.RECIPES_STAGES.isEmpty() || !StageContainer.INSTANCE.RECIPES_STAGES.containsKey(ModRecipes.OFFERING_TYPE)) return;

        var recipe = cir.getReturnValue();

        Optional<IOwnerBlock> d1 = thisEntity.getCapability(SupportBlockData.BLOCK_OWNER).resolve();
        if (d1.isPresent() && thisEntity.getLevel().getServer() != null) {
            IOwnerBlock ownerBlock = d1.get();
            RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
            if(recipeBlockType != null) {
                PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                if(player != null) {
                    if(!player.hasStage(recipeBlockType.stage)) {
                        cir.setReturnValue(null);
                    }
                }
            }
        }
    }
}
