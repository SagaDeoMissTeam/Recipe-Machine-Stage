package net.sdm.recipemachinestage.mixin.integration.extendedcrafting;


import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.CachedRecipe;
import com.blakebr0.extendedcrafting.api.crafting.ICombinationRecipe;
import com.blakebr0.extendedcrafting.api.crafting.IEnderCrafterRecipe;
import com.blakebr0.extendedcrafting.tileentity.CraftingCoreTileEntity;
import net.sdm.recipemachinestage.RMSCapability;
import net.sdm.recipemachinestage.api.capability.IOwnerBlock;
import net.sdm.recipemachinestage.api.stage.StageContainer;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import net.sdm.recipemachinestage.utils.PlayerHelper;
import net.sdm.recipemachinestage.utils.RecipeStagesUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = CraftingCoreTileEntity.class, remap = false)
public class CraftingCoreTileEntityMixin {

    @Shadow
    @Final
    private CachedRecipe<IEnderCrafterRecipe> recipe;
    @Shadow @Final private BaseItemStackHandler inventory;
    @Unique
    private CraftingCoreTileEntity recipe_machine_stage$thisEntity = RecipeStagesUtil.cast(this);


    @Inject(method = "getActiveRecipe", at = @At("RETURN"), cancellable = true)
    private void sdm$getActiveRecipe(CallbackInfoReturnable<ICombinationRecipe> cir){
        ICombinationRecipe recipe = cir.getReturnValue();
        if(recipe != null) {

            if(!StageContainer.hasRecipes(recipe.getType())) {
                return;
            }

            Optional<IOwnerBlock> d1 = recipe_machine_stage$thisEntity.getCapability(RMSCapability.BLOCK_OWNER).resolve();
            if (d1.isPresent() && recipe_machine_stage$thisEntity.getLevel().getServer() != null) {
                IOwnerBlock ownerBlock = d1.get();
                RecipeBlockType recipeBlockType =  StageContainer.getRecipeData(recipe.getType(), recipe.getId());
                if(recipeBlockType != null) {
                    PlayerHelper.@Nullable RMSStagePlayerData player = PlayerHelper.getPlayerByGameProfile(recipe_machine_stage$thisEntity.getLevel().getServer(), ownerBlock.getOwner());
                    if(player != null) {
                        if(!player.hasStage(recipeBlockType.stage)) {
                            cir.setReturnValue(null);
                        }
                    }
                }
            }
        }
    }
}
