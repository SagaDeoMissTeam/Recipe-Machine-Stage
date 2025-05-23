package net.sdm.recipemachinestage.api.stage;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeType;
import net.sdm.recipemachinestage.RecipeMachineStage;
import net.sdm.recipemachinestage.api.stage.type.RecipeBlockType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class StageContainer extends SimplePreparableReloadListener<Void> {

    public static StageContainer INSTANCE = new StageContainer();
    private static final List<IStage> REGISTER_STAGE_MODS = new ArrayList<>();

    public Map<RecipeType<?>, List<RecipeBlockType>> RECIPES_STAGES = new HashMap<>();
    public Map<RecipeType<?>, List<RecipeBlockType>> RECIPES_STAGES_KUBEJS = new HashMap<>();

    public static List<IStage> getStageMods() {
        return REGISTER_STAGE_MODS.stream().toList();
    }

    public static void registerStageContainer(Supplier<IStage> stageSupplier) {
        IStage stages = stageSupplier.get();

        if(!stages.isModLoaded()) return;

        REGISTER_STAGE_MODS.add(stages);

        stages.registerEventsServer();
        EnvExecutor.runInEnv(Env.CLIENT, () -> stages::registerEventsClient);

        RecipeMachineStage.LOGGER.info("Registered {} stages container", stages.getClass().getName());
    }

    public static List<RecipeBlockType> getRecipe(RecipeType<?> blockEntityClass) {
        return INSTANCE.RECIPES_STAGES.getOrDefault(blockEntityClass, new ArrayList<>());
    }

    public static boolean hasRecipes(RecipeType<?> blockEntityClass) {
        boolean flag = blockEntityClass != null;
        if(flag) {
            flag = INSTANCE.RECIPES_STAGES.containsKey(blockEntityClass);
        }

        return !INSTANCE.RECIPES_STAGES.isEmpty() && flag;
    }

    public static Optional<RecipeBlockType> getRecipeDataOptional(RecipeType<?> blockEntityClass, ResourceLocation recipeID) {
        return Optional.ofNullable(getRecipeData(blockEntityClass, recipeID));
    }

    @Nullable
    public static RecipeBlockType getRecipeData(RecipeType<?> blockEntityClass, ResourceLocation recipeID) {
        List<RecipeBlockType> TYPES = INSTANCE.RECIPES_STAGES.getOrDefault(blockEntityClass, new ArrayList<>());
        if(TYPES.isEmpty()) return null;

        for (RecipeBlockType type : TYPES) {
            if(type.contains(recipeID)) return type;
        }

        return null;
    }

    @Nullable
    public static List<RecipeBlockType> getAllRecipeData(RecipeType<?> blockEntityClass, ResourceLocation recipeID) {
        List<RecipeBlockType> TYPES = INSTANCE.RECIPES_STAGES.getOrDefault(blockEntityClass, new ArrayList<>());
        if(TYPES.isEmpty()) return null;

        List<RecipeBlockType> d1 = new ArrayList<>();

        for (RecipeBlockType type : TYPES) {
            if(type.recipesID.contains(recipeID)) {
                d1.add(type);
            }
        }

        return d1;
    }


    public static void registerRecipe(RecipeType<?> recipeType, ResourceLocation recipeID, String stage) {
        registerRecipe(recipeType, new ArrayList<>(List.of(recipeID)), stage);
    }

    public static void registerRecipe(RecipeType<?> recipeType, List<ResourceLocation> recipeID, String stage) {
        try {
            RecipeBlockType recipeBlockType = new RecipeBlockType(stage);
            recipeBlockType.recipesID.addAll(recipeID);

            if (StageContainer.INSTANCE.RECIPES_STAGES.containsKey(recipeType)) {
                boolean added = false;
                for (RecipeBlockType value : StageContainer.INSTANCE.RECIPES_STAGES.get(recipeType)) {
                    if (value.addRecipeType(recipeBlockType)) return;
                }
                if (!added) {
                    StageContainer.INSTANCE.RECIPES_STAGES.get(recipeType).add(recipeBlockType);
                    RecipeMachineStage.LOGGER.info("Added recipes " + recipeID + " to stage " + stage);
                }
            } else {
                StageContainer.INSTANCE.RECIPES_STAGES.put(recipeType, new ArrayList<>(List.of(recipeBlockType)));
                RecipeMachineStage.LOGGER.info("Added recipes " + recipeID + " to stage " + stage);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void registerRecipeJS(RecipeType<?> recipeType, ResourceLocation recipeID, String stage) {
        registerRecipeJS(recipeType, new ArrayList<>(List.of(recipeID)), stage);
    }

    public static void registerRecipeJS(RecipeType<?> recipeType, List<ResourceLocation> recipeID, String stage) {
        try {
            RecipeBlockType recipeBlockType = new RecipeBlockType(stage);
            recipeBlockType.recipesID.addAll(recipeID);

            if (StageContainer.INSTANCE.RECIPES_STAGES_KUBEJS.containsKey(recipeType)) {
                boolean added = false;
                for (RecipeBlockType value : StageContainer.INSTANCE.RECIPES_STAGES_KUBEJS.get(recipeType)) {
                    if (value.addRecipeType(recipeBlockType)) return;
                }
                if (!added) {
                    StageContainer.INSTANCE.RECIPES_STAGES_KUBEJS.get(recipeType).add(recipeBlockType);
                    RecipeMachineStage.LOGGER.info("Added recipes " + recipeID + " to stage " + stage);
                }
            } else {
                StageContainer.INSTANCE.RECIPES_STAGES_KUBEJS.put(recipeType, new ArrayList<>(List.of(recipeBlockType)));
                RecipeMachineStage.LOGGER.info("Added recipes " + recipeID + " to stage " + stage);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }



    public static Map<RecipeType<?>, List<RecipeBlockType>> getLockedRecipes(IStageData data, boolean isLocked) {
        Map<RecipeType<?>, List<RecipeBlockType>> map = new HashMap<>();

        if(isLocked) {
            for (Map.Entry<RecipeType<?>, List<RecipeBlockType>> d1 : INSTANCE.RECIPES_STAGES.entrySet()) {
                List<RecipeBlockType> f1 = new ArrayList<>();
                for (RecipeBlockType recipeBlockType : d1.getValue()) {
                    if(!data.hasStage(recipeBlockType.stage)) {
                        f1.add(recipeBlockType);
                    }
                }
                map.put(d1.getKey(), f1);
            }
        } else {
            for (Map.Entry<RecipeType<?>, List<RecipeBlockType>> d1 : INSTANCE.RECIPES_STAGES.entrySet()) {
                List<RecipeBlockType> f1 = new ArrayList<>();
                for (RecipeBlockType recipeBlockType : d1.getValue()) {
                    if(data.hasStage(recipeBlockType.stage)) {
                        f1.add(recipeBlockType);
                    }
                }
                map.put(d1.getKey(), f1);
            }
        }

        return map;
    }

    @Override
    protected Void prepare(ResourceManager p_10796_, ProfilerFiller p_10797_) {
        return null;
    }

    @Override
    protected void apply(Void p_10793_, ResourceManager p_10794_, ProfilerFiller p_10795_) {
        RECIPES_STAGES.clear();
        RECIPES_STAGES.putAll(RECIPES_STAGES_KUBEJS);
    }
}
