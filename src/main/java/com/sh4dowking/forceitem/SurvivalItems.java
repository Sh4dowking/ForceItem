package com.sh4dowking.forceitem;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;

/**
 * SurvivalItems - Curated list of obtainable items in survival mode
 * 
 * This class maintains a comprehensive whitelist of materials that can be
 * legitimately obtained in survival Minecraft. Used by ForceItem to ensure
 * only fair and achievable targets are assigned to players.
 * 
 * Categories include:
 * - Basic blocks (stone, dirt, ores)
 * - Wood types and variants
 * - Tools and weapons
 * - Food items
 * - Decorative blocks
 * - Redstone components
 * 
 * @author Sh4dowking
 * @version 1.0
 */
public class SurvivalItems {
    private static final Random RANDOM = new Random();
    
    /**
     * Comprehensive list of survival-obtainable materials
     * Excludes creative-only blocks, command blocks, and unobtainable items
     */
    private static final List<Material> SURVIVAL_ITEMS = Arrays.asList(
        // Basic blocks and terrain materials
        Material.STONE, Material.GRANITE, Material.POLISHED_GRANITE, Material.DIORITE, Material.POLISHED_DIORITE,
        Material.ANDESITE, Material.POLISHED_ANDESITE, Material.DEEPSLATE, Material.COBBLED_DEEPSLATE,
        Material.POLISHED_DEEPSLATE, Material.CALCITE, Material.TUFF, Material.DRIPSTONE_BLOCK,
        Material.GRASS_BLOCK, Material.DIRT, Material.COARSE_DIRT, Material.PODZOL, Material.ROOTED_DIRT,
        Material.MUD, Material.CLAY, Material.GRAVEL, Material.SAND, Material.RED_SAND,
        
        // Wood types and wooden items
        Material.OAK_LOG, Material.OAK_WOOD, Material.STRIPPED_OAK_LOG, Material.STRIPPED_OAK_WOOD,
        Material.OAK_PLANKS, Material.OAK_STAIRS, Material.OAK_SLAB, Material.OAK_FENCE, Material.OAK_FENCE_GATE,
        Material.OAK_DOOR, Material.OAK_TRAPDOOR, Material.OAK_PRESSURE_PLATE, Material.OAK_BUTTON,
        Material.OAK_SIGN, Material.OAK_HANGING_SIGN, Material.OAK_BOAT, Material.OAK_CHEST_BOAT,
        Material.SPRUCE_LOG, Material.SPRUCE_WOOD, Material.STRIPPED_SPRUCE_LOG, Material.STRIPPED_SPRUCE_WOOD,
        Material.SPRUCE_PLANKS, Material.SPRUCE_STAIRS, Material.SPRUCE_SLAB, Material.SPRUCE_FENCE, Material.SPRUCE_FENCE_GATE,
        Material.SPRUCE_DOOR, Material.SPRUCE_TRAPDOOR, Material.SPRUCE_PRESSURE_PLATE, Material.SPRUCE_BUTTON,
        Material.SPRUCE_SIGN, Material.SPRUCE_HANGING_SIGN, Material.SPRUCE_BOAT, Material.SPRUCE_CHEST_BOAT,
        Material.BIRCH_LOG, Material.BIRCH_WOOD, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_BIRCH_WOOD,
        Material.BIRCH_PLANKS, Material.BIRCH_STAIRS, Material.BIRCH_SLAB, Material.BIRCH_FENCE, Material.BIRCH_FENCE_GATE,
        Material.BIRCH_DOOR, Material.BIRCH_TRAPDOOR, Material.BIRCH_PRESSURE_PLATE, Material.BIRCH_BUTTON,
        Material.BIRCH_SIGN, Material.BIRCH_HANGING_SIGN, Material.BIRCH_BOAT, Material.BIRCH_CHEST_BOAT,
        Material.JUNGLE_LOG, Material.JUNGLE_WOOD, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_JUNGLE_WOOD,
        Material.JUNGLE_PLANKS, Material.JUNGLE_STAIRS, Material.JUNGLE_SLAB, Material.JUNGLE_FENCE, Material.JUNGLE_FENCE_GATE,
        Material.JUNGLE_DOOR, Material.JUNGLE_TRAPDOOR, Material.JUNGLE_PRESSURE_PLATE, Material.JUNGLE_BUTTON,
        Material.JUNGLE_SIGN, Material.JUNGLE_HANGING_SIGN, Material.JUNGLE_BOAT, Material.JUNGLE_CHEST_BOAT,
        Material.ACACIA_LOG, Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_ACACIA_WOOD,
        Material.ACACIA_PLANKS, Material.ACACIA_STAIRS, Material.ACACIA_SLAB, Material.ACACIA_FENCE, Material.ACACIA_FENCE_GATE,
        Material.ACACIA_DOOR, Material.ACACIA_TRAPDOOR, Material.ACACIA_PRESSURE_PLATE, Material.ACACIA_BUTTON,
        Material.ACACIA_SIGN, Material.ACACIA_HANGING_SIGN, Material.ACACIA_BOAT, Material.ACACIA_CHEST_BOAT,
        Material.DARK_OAK_LOG, Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_WOOD,
        Material.DARK_OAK_PLANKS, Material.DARK_OAK_STAIRS, Material.DARK_OAK_SLAB, Material.DARK_OAK_FENCE, Material.DARK_OAK_FENCE_GATE,
        Material.DARK_OAK_DOOR, Material.DARK_OAK_TRAPDOOR, Material.DARK_OAK_PRESSURE_PLATE, Material.DARK_OAK_BUTTON,
        Material.DARK_OAK_SIGN, Material.DARK_OAK_HANGING_SIGN, Material.DARK_OAK_BOAT, Material.DARK_OAK_CHEST_BOAT,
        Material.MANGROVE_LOG, Material.MANGROVE_WOOD, Material.STRIPPED_MANGROVE_LOG, Material.STRIPPED_MANGROVE_WOOD,
        Material.MANGROVE_PLANKS, Material.MANGROVE_STAIRS, Material.MANGROVE_SLAB, Material.MANGROVE_FENCE, Material.MANGROVE_FENCE_GATE,
        Material.MANGROVE_DOOR, Material.MANGROVE_TRAPDOOR, Material.MANGROVE_PRESSURE_PLATE, Material.MANGROVE_BUTTON,
        Material.MANGROVE_SIGN, Material.MANGROVE_HANGING_SIGN, Material.MANGROVE_BOAT, Material.MANGROVE_CHEST_BOAT,
        Material.CHERRY_LOG, Material.CHERRY_WOOD, Material.STRIPPED_CHERRY_LOG, Material.STRIPPED_CHERRY_WOOD,
        Material.CHERRY_PLANKS, Material.CHERRY_STAIRS, Material.CHERRY_SLAB, Material.CHERRY_FENCE, Material.CHERRY_FENCE_GATE,
        Material.CHERRY_DOOR, Material.CHERRY_TRAPDOOR, Material.CHERRY_PRESSURE_PLATE, Material.CHERRY_BUTTON,
        Material.CHERRY_SIGN, Material.CHERRY_HANGING_SIGN, Material.CHERRY_BOAT, Material.CHERRY_CHEST_BOAT,
        Material.BAMBOO_BLOCK, Material.STRIPPED_BAMBOO_BLOCK, Material.BAMBOO_PLANKS, Material.BAMBOO_STAIRS,
        Material.BAMBOO_SLAB, Material.BAMBOO_FENCE, Material.BAMBOO_FENCE_GATE, Material.BAMBOO_DOOR,
        Material.BAMBOO_TRAPDOOR, Material.BAMBOO_PRESSURE_PLATE, Material.BAMBOO_BUTTON, Material.BAMBOO_SIGN,
        Material.BAMBOO_HANGING_SIGN, Material.BAMBOO_RAFT, Material.BAMBOO_CHEST_RAFT,
        
        // Stone types
        Material.COBBLESTONE, Material.COBBLESTONE_STAIRS, Material.COBBLESTONE_SLAB, Material.COBBLESTONE_WALL,
        Material.MOSSY_COBBLESTONE, Material.MOSSY_COBBLESTONE_STAIRS, Material.MOSSY_COBBLESTONE_SLAB, Material.MOSSY_COBBLESTONE_WALL,
        Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS, Material.CRACKED_STONE_BRICKS, Material.CHISELED_STONE_BRICKS,
        Material.STONE_BRICK_STAIRS, Material.STONE_BRICK_SLAB, Material.STONE_BRICK_WALL,
        Material.MOSSY_STONE_BRICK_STAIRS, Material.MOSSY_STONE_BRICK_SLAB, Material.MOSSY_STONE_BRICK_WALL,
        Material.SMOOTH_STONE, Material.SMOOTH_STONE_SLAB,
        
        // Ores and minerals
        Material.COAL, Material.COAL_BLOCK, Material.RAW_COPPER, Material.RAW_COPPER_BLOCK,
        Material.COPPER_BLOCK, Material.CUT_COPPER, Material.CUT_COPPER_STAIRS, Material.CUT_COPPER_SLAB,
        Material.RAW_IRON, Material.RAW_IRON_BLOCK, Material.IRON_INGOT, Material.IRON_BLOCK, Material.IRON_NUGGET, Material.RAW_GOLD,
        Material.RAW_GOLD_BLOCK, Material.GOLD_INGOT, Material.GOLD_BLOCK, Material.GOLD_NUGGET,
        Material.LAPIS_LAZULI, Material.LAPIS_BLOCK, Material.DIAMOND, Material.DIAMOND_BLOCK, Material.REDSTONE, Material.REDSTONE_BLOCK,
        Material.EMERALD, Material.EMERALD_BLOCK, Material.NETHERITE_SCRAP, Material.ANCIENT_DEBRIS, Material.AMETHYST_SHARD, Material.AMETHYST_BLOCK,
        
        // Tools and weapons
        Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE,
        Material.STONE_SWORD, Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_SHOVEL, Material.STONE_HOE,
        Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_HOE,
        Material.GOLDEN_SWORD, Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE,
        Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE,
        Material.BOW, Material.CROSSBOW, Material.TRIDENT, Material.SHIELD, Material.FISHING_ROD,
        Material.FLINT_AND_STEEL, Material.SHEARS, Material.COMPASS, Material.CLOCK, Material.SPYGLASS,
        
        // Armor
        Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
        Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
        Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
        Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
        
        // Food
        Material.APPLE, Material.GOLDEN_APPLE, Material.MELON_SLICE, Material.SWEET_BERRIES,
        Material.GLOW_BERRIES, Material.CARROT, Material.GOLDEN_CARROT, Material.POTATO, Material.BAKED_POTATO,
        Material.POISONOUS_POTATO, Material.BEETROOT, Material.BEETROOT_SOUP, Material.BREAD, Material.COOKIE,
        Material.CAKE, Material.PUMPKIN_PIE, Material.ROTTEN_FLESH, Material.SPIDER_EYE, Material.CHICKEN,
        Material.COOKED_CHICKEN, Material.BEEF, Material.COOKED_BEEF, Material.PORKCHOP, Material.COOKED_PORKCHOP,
        Material.MUTTON, Material.COOKED_MUTTON, Material.RABBIT, Material.COOKED_RABBIT, Material.RABBIT_STEW,
        Material.COD, Material.COOKED_COD, Material.SALMON, Material.COOKED_SALMON, Material.TROPICAL_FISH,
        Material.PUFFERFISH, Material.DRIED_KELP, Material.KELP, Material.MUSHROOM_STEW, Material.HONEY_BOTTLE, Material.MILK_BUCKET,
        
        // Plants and farming
        Material.WHEAT, Material.WHEAT_SEEDS, Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.JACK_O_LANTERN,
        Material.MELON, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Material.BEETROOT_SEEDS, Material.COCOA_BEANS,
        Material.SUGAR_CANE, Material.BAMBOO, Material.CACTUS, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
        Material.MUSHROOM_STEM, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.VINE,
        Material.LILY_PAD, Material.SEA_PICKLE, Material.SEAGRASS, Material.TALL_SEAGRASS,
        
        // Flowers
        Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET,
        Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY,
        Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY, Material.SUNFLOWER, Material.LILAC,
        Material.ROSE_BUSH, Material.PEONY, Material.DEAD_BUSH, Material.AZALEA, Material.FLOWERING_AZALEA,
        
        // Nether items
        Material.NETHERRACK, Material.NETHER_BRICKS, Material.NETHER_BRICK_STAIRS, Material.NETHER_BRICK_SLAB,
        Material.NETHER_BRICK_WALL, Material.NETHER_BRICK_FENCE, Material.RED_NETHER_BRICKS,
        Material.RED_NETHER_BRICK_STAIRS, Material.RED_NETHER_BRICK_SLAB, Material.RED_NETHER_BRICK_WALL,
        Material.BASALT, Material.POLISHED_BASALT, Material.SMOOTH_BASALT, Material.BLACKSTONE,
        Material.POLISHED_BLACKSTONE, Material.POLISHED_BLACKSTONE_BRICKS, Material.CRACKED_POLISHED_BLACKSTONE_BRICKS,
        Material.CHISELED_POLISHED_BLACKSTONE, Material.GILDED_BLACKSTONE, Material.SOUL_SAND, Material.SOUL_SOIL,
        Material.MAGMA_BLOCK, Material.NETHER_WART, Material.NETHER_WART_BLOCK, Material.WARPED_NYLIUM,
        Material.CRIMSON_NYLIUM, Material.SHROOMLIGHT, Material.WEEPING_VINES, Material.TWISTING_VINES,
        Material.NETHER_SPROUTS, Material.CRIMSON_ROOTS, Material.WARPED_ROOTS,
        Material.BLAZE_ROD, Material.BLAZE_POWDER, Material.GHAST_TEAR, Material.GUNPOWDER, Material.MAGMA_CREAM,
        Material.GLOWSTONE_DUST, Material.GLOWSTONE, Material.QUARTZ, Material.QUARTZ_BLOCK, Material.QUARTZ_STAIRS,
        Material.QUARTZ_SLAB, Material.CHISELED_QUARTZ_BLOCK, Material.QUARTZ_PILLAR, Material.SMOOTH_QUARTZ,
        Material.SMOOTH_QUARTZ_STAIRS, Material.SMOOTH_QUARTZ_SLAB,
        
        // Redstone
        Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH, Material.REPEATER, Material.COMPARATOR,
        Material.LEVER, Material.STONE_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
        Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.STONE_BUTTON, Material.TRIPWIRE_HOOK,
        Material.DAYLIGHT_DETECTOR, Material.REDSTONE_LAMP, Material.TNT, Material.DISPENSER, Material.DROPPER,
        Material.HOPPER, Material.PISTON, Material.STICKY_PISTON, Material.SLIME_BLOCK, Material.HONEY_BLOCK,
        Material.OBSERVER, Material.TARGET, Material.LIGHTNING_ROD,
        
        // Utility blocks
        Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST, Material.BARREL,
        Material.FURNACE, Material.BLAST_FURNACE, Material.SMOKER, Material.CRAFTING_TABLE, Material.CARTOGRAPHY_TABLE,
        Material.FLETCHING_TABLE, Material.SMITHING_TABLE, Material.GRINDSTONE, Material.LOOM, Material.STONECUTTER,
        Material.COMPOSTER, Material.CAULDRON,
        Material.ANVIL, Material.ENCHANTING_TABLE, Material.BOOKSHELF,
        Material.LECTERN, Material.BREWING_STAND, Material.END_CRYSTAL, Material.ITEM_FRAME, Material.GLOW_ITEM_FRAME,
        Material.PAINTING, Material.ARMOR_STAND, Material.FLOWER_POT,
        
        // Dyes and wool
        Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.MAGENTA_WOOL, Material.LIGHT_BLUE_WOOL,
        Material.YELLOW_WOOL, Material.LIME_WOOL, Material.PINK_WOOL, Material.GRAY_WOOL,
        Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL,
        Material.BROWN_WOOL, Material.GREEN_WOOL, Material.RED_WOOL, Material.BLACK_WOOL,
        Material.WHITE_DYE, Material.ORANGE_DYE, Material.MAGENTA_DYE, Material.LIGHT_BLUE_DYE,
        Material.YELLOW_DYE, Material.LIME_DYE, Material.PINK_DYE, Material.GRAY_DYE,
        Material.LIGHT_GRAY_DYE, Material.CYAN_DYE, Material.PURPLE_DYE, Material.BLUE_DYE,
        Material.BROWN_DYE, Material.GREEN_DYE, Material.RED_DYE, Material.BLACK_DYE,
        
        // Glass and terracotta
        Material.GLASS, Material.WHITE_STAINED_GLASS, Material.ORANGE_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS,
        Material.LIGHT_BLUE_STAINED_GLASS, Material.YELLOW_STAINED_GLASS, Material.LIME_STAINED_GLASS,
        Material.PINK_STAINED_GLASS, Material.GRAY_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS,
        Material.CYAN_STAINED_GLASS, Material.PURPLE_STAINED_GLASS, Material.BLUE_STAINED_GLASS,
        Material.BROWN_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.RED_STAINED_GLASS,
        Material.BLACK_STAINED_GLASS, Material.TERRACOTTA, Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA,
        Material.MAGENTA_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA, Material.YELLOW_TERRACOTTA,
        Material.LIME_TERRACOTTA, Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA,
        Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA,
        Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA, Material.BLACK_TERRACOTTA,
        
        // Miscellaneous items
        Material.STICK, Material.BOWL, Material.STRING, Material.FEATHER, Material.FLINT, Material.LEATHER,
        Material.RABBIT_HIDE, Material.PAPER, Material.BOOK, Material.SLIME_BALL, Material.EGG, Material.INK_SAC,
        Material.GLOW_INK_SAC, Material.BONE, Material.BONE_MEAL, Material.SUGAR,
        Material.LEAD, Material.PRISMARINE_SHARD, Material.PRISMARINE_CRYSTALS
    );

    /**
     * Get a random survival-obtainable material from the whitelist
     * @return A random Material that can be obtained in survival mode
     */
    public static Material getRandomMaterial() {
        return SURVIVAL_ITEMS.get(RANDOM.nextInt(SURVIVAL_ITEMS.size()));
    }

    /**
     * Get the full list of survival-obtainable materials
     * @return List of all survival-obtainable materials
     */
    public static List<Material> getAllSurvivalItems() {
        return SURVIVAL_ITEMS;
    }

    /**
     * Check if a material is in the survival items whitelist
     * @param material The material to check
     * @return true if the material is survival-obtainable, false otherwise
     */
    public static boolean isSurvivalObtainable(Material material) {
        return SURVIVAL_ITEMS.contains(material);
    }

    /**
     * Get the total count of survival-obtainable materials
     * @return The number of materials in the whitelist
     */
    public static int getItemCount() {
        return SURVIVAL_ITEMS.size();
    }
}
