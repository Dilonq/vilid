package djh.vilid.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import djh.vilid.ideology.politics.Ideology;
import djh.vilid.ideology.politics.Party;
import djh.vilid.nation.NationData;
import djh.vilid.villager.VillagerExt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.Collectors;

import static djh.vilid.ideology.politics.Ideology.adjustIdeology;
import static djh.vilid.ideology.politics.Viewpoint.adjustVillager;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModCommands {

    private static String up(String s) {
        return s.toUpperCase(Locale.ROOT);
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("rename")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("newName", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String newName = up(StringArgumentType.getString(ctx, "newName"));
                            return renameItemInHand(ctx.getSource(), newName);
                        }))
        );

        dispatcher.register(literal("poll")
                .requires(source -> source.hasPermissionLevel(0))
                .then(argument("nation", StringArgumentType.string())
                        .executes(ctx -> {
                            String nation = up(StringArgumentType.getString(ctx, "nation"));
                            return pollNation(ctx.getSource(), nation);
                        }))
        );

        dispatcher.register(literal("medianhappiness")
                .requires(source -> source.hasPermissionLevel(0))
                .then(argument("nation", StringArgumentType.string())
                        .executes(ctx -> {
                            String nation = up(StringArgumentType.getString(ctx, "nation"));
                            return getMedianHappiness(ctx.getSource(), nation);
                        }))
        );

        dispatcher.register(literal("registernation")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String name = up(StringArgumentType.getString(ctx, "name"));
                            return registerNation(ctx.getSource(), name);
                        }))
        );

        dispatcher.register(literal("listnations")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(ctx -> getNations(ctx.getSource()))
        );

        dispatcher.register(literal("listideologies")
                .requires(source -> source.hasPermissionLevel(0))
                .executes(ctx -> listIdeologies(ctx.getSource()))
        );

        dispatcher.register(literal("unregisternation")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String name = up(StringArgumentType.getString(ctx, "name"));
                            return unregisterNation(ctx.getSource(), name);
                        }))
        );

        dispatcher.register(literal("registerparty")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("nation", StringArgumentType.string())
                        .then(argument("party", StringArgumentType.string())
                                .then(argument("ideology", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String nation = up(StringArgumentType.getString(ctx, "nation"));
                                            String partyName = up(StringArgumentType.getString(ctx, "party"));
                                            String ideologyStr = up(StringArgumentType.getString(ctx, "ideology"));

                                            return registerParty(ctx.getSource(), nation,partyName,ideologyStr);
                                        }))))
        );

        dispatcher.register(literal("unregisterparty")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("nation", StringArgumentType.string())
                        .then(argument("party", StringArgumentType.string())
                                .executes(ctx -> {
                                    String nation = up(StringArgumentType.getString(ctx, "nation"));
                                    String partyName = up(StringArgumentType.getString(ctx, "party"));

                                    return unregisterParty(ctx.getSource(),nation,partyName);
                                })))
        );

        dispatcher.register(literal("elect")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("nation", StringArgumentType.string())
                        .then(argument("party", StringArgumentType.string())
                                .executes(ctx -> {
                                    String nation = up(StringArgumentType.getString(ctx, "nation"));
                                    String partyName = up(StringArgumentType.getString(ctx, "party"));
                                    return electParty(ctx.getSource(), nation, partyName);
                                })))
        );

        dispatcher.register(literal("listparties")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("nation", StringArgumentType.string())
                        .executes(ctx -> {
                            String nation = up(StringArgumentType.getString(ctx, "nation"));
                            NationData data = NationData.get(ctx.getSource().getWorld());
                            if (!data.contains(nation)) {
                                ctx.getSource().sendError(Text.literal("Nation '" + nation + "' is not registered."));
                                return 0;
                            }

                            var parties = data.getParties(nation);
                            if (parties.isEmpty()) {
                                ctx.getSource().sendFeedback(
                                        () -> Text.literal("No parties registered for nation '" + nation + "'"), false);
                            } else {
                                ctx.getSource().sendFeedback(
                                        () -> Text.literal("Parties for '" + nation + "':"), false);
                                for (Party p : parties) {
                                    ctx.getSource().sendFeedback(
                                            () -> Text.literal("- " + p.getName() + " [" + p.getIdeology() + "]"),
                                            false);
                                }
                            }
                            return 1;
                        }))
        );

        dispatcher.register(literal("getrulingparty")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("nation", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String nation = up(StringArgumentType.getString(ctx, "nation"));
                            return getRulingParty(ctx.getSource(), nation);
                        }))
        );
    }

    public static int unregisterParty(ServerCommandSource source,String nation,String partyName) {
        NationData data = NationData.get(source.getWorld());
        if (!data.contains(nation)) {
            source.sendError(Text.literal("Nation '" + nation + "' is not registered."));
            return 0;
        }

        boolean removed = data.removeParty(nation, partyName);
        if (removed) {
            source.sendFeedback(
                    () -> Text.literal("Unregistered party '" + partyName + "' from nation '" + nation + "'"), true);
            return 1;
        } else {
            source.sendError(
                    Text.literal("Party '" + partyName + "' not found in nation '" + nation + "'"));
            return 0;
        }
    }

    public static int registerParty(ServerCommandSource source,String nation, String partyName, String ideologyStr) {
        Ideology ideology;
        try {
            ideology = Ideology.valueOf(ideologyStr);
        } catch (IllegalArgumentException e) {
            source.sendError(Text.literal("Invalid ideology: " + ideologyStr));
            return 0;
        }

        NationData data = NationData.get(source.getWorld());
        if (!data.contains(nation)) {
            source.sendError(Text.literal("Nation '" + nation + "' is not registered."));
            return 0;
        }

        Party party = new Party(partyName, ideology, 0);
        data.addParty(nation, party);

        source.sendFeedback(
                () -> Text.literal("Registered party '" + partyName + "' in nation '" + nation + "'"), true);

        return 1;
    }

    private static int pollNation(ServerCommandSource source, String nation) {
        ServerWorld world = source.getWorld();
        NationData data = NationData.get(world);

        if (!data.contains(nation)) {
            source.sendError(Text.literal("Nation '" + nation + "' is not registered."));
            return 0;
        }

        // Start counts at 0 for all ideologies
        Map<Ideology, Long> counts = new EnumMap<>(Ideology.class);
        for (Ideology ideology : Ideology.values()) {
            counts.put(ideology, 0L);
        }

        // 1. Count live villagers in this world
        for (Entity entity : world.iterateEntities()) {
            if (entity instanceof VillagerEntity villager) {
                VillagerExt ext = (VillagerExt) villager;
                if (ext.getViewpoint().getNation().equalsIgnoreCase(nation)) {
                    counts.merge(ext.getViewpoint().getIdeology(), 1L, Long::sum);
                }
            }
        }

        // 2. Count cached villagers not currently loaded
        for (var entry : data.villagerStates.entrySet()) {
            UUID uuid = entry.getKey();
            NationData.VillagerSnapshot snapshot = entry.getValue();

            if (!snapshot.nation().equalsIgnoreCase(nation)) continue;
            if (world.getEntity(uuid) != null) continue; // skip if loaded

            counts.merge(snapshot.ideology(), 1L, Long::sum);
        }

        // Check if there are any villagers at all
        boolean hasAny = counts.values().stream().anyMatch(c -> c > 0);
        if (!hasAny) {
            source.sendFeedback(() -> Text.literal("No villagers found in nation '" + nation + "'."), false);
            return 0;
        }

        // Build output
        StringBuilder sb = new StringBuilder("Ideologies in '" + nation + "':\n");
        for (Ideology ideology : Ideology.values()) {
            sb.append("• ").append(ideology.name()).append(": ").append(counts.get(ideology)).append("\n");
        }

        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        return 1;
    }


    private static int getMedianHappiness(ServerCommandSource source, String nation) {
        ServerWorld world = source.getWorld();
        NationData data = NationData.get(world);

        List<Integer> happinessList = data.villagerStates.values().stream()
                .filter(v -> v.nation().equalsIgnoreCase(nation))
                .map(NationData.VillagerSnapshot::happiness)
                .sorted()
                .toList();

        if (happinessList.isEmpty()) {
            source.sendFeedback(() -> Text.literal("No villagers found in nation '" + nation + "'."), false);
            return 0;
        }

        double median;
        int size = happinessList.size();
        median = (size % 2 == 1)
                ? happinessList.get(size / 2)
                : (happinessList.get(size / 2 - 1) + happinessList.get(size / 2)) / 2.0;

        source.sendFeedback(() -> Text.literal(
                String.format(Locale.ROOT, "Median happiness of nation '%s' is: %.1f", nation, median)), false);
        return 1;
    }

    private static int getRulingParty(ServerCommandSource source, String nation) {
        ServerWorld world = source.getWorld();
        NationData data = NationData.get(world);

        if (!data.contains(nation)) {
            source.sendError(Text.literal("Nation '" + nation + "' does not exist."));
            return 0;
        }

        String rulingParty = data.getRulingParty(nation);
        if (rulingParty == null || rulingParty.equals("(none)")) {
            source.sendFeedback(() -> Text.literal("ℹ Nation '" + nation + "' has no ruling party."), false);
            return 1;
        }

        source.sendFeedback(() -> Text.literal("Ruling party of '" + nation + "' is: " + up(rulingParty)), false);
        return 1;
    }

    private static int electParty(ServerCommandSource source, String nation, String partyName) {
        ServerWorld world = source.getWorld();
        NationData data = NationData.get(world);

        if (!data.contains(nation)) {
            source.sendError(Text.literal("Nation '" + nation + "' does not exist."));
            return 0;
        }

        List<Party> parties = data.getParties(nation);
        Party electedParty = parties.stream()
                .filter(p -> up(p.getName()).equals(partyName))
                .findFirst()
                .orElse(null);

        if (electedParty == null) {
            source.sendError(Text.literal("Party '" + partyName + "' does not exist in nation '" + nation + "'."));
            return 0;
        }

        // old ruling ideology (if any)
        String oldRulingPartyName = data.getRulingParty(nation);
        Ideology oldIdeology = null;
        if (oldRulingPartyName != null && !oldRulingPartyName.equals("(none)")) {
            Party oldParty = parties.stream()
                    .filter(p -> up(p.getName()).equals(up(oldRulingPartyName)))
                    .findFirst()
                    .orElse(null);
            if (oldParty != null) oldIdeology = oldParty.getIdeology();
        }

        // set new ruling party (store uppercase)
        data.setRulingParty(nation, partyName);
        source.sendFeedback(() -> Text.literal("Elected '" + partyName + "' as ruling party of '" + nation + "'."), true);

//        int adjusted = 0;
//        Set<UUID> processed = new HashSet<>();
//
//        // loaded villagers
//        for (Entity entity : world.iterateEntities()) {
//            if (!(entity instanceof VillagerEntity villager)) continue;
//
//            VillagerExt ext = (VillagerExt) villager;
//            if (!ext.getViewpoint().getNation().equalsIgnoreCase(nation)) continue;
//
//            adjusted += adjustVillager(ext.getViewpoint(), electedParty.getIdeology(), oldIdeology);
//
//            data.updateVillager(
//                    villager.getUuid(),
//                    ext.getViewpoint().getNation(),
//                    ext.getViewpoint().getHappy(),
//                    ext.getViewpoint().getIdeology()
//            );
//
//            processed.add(villager.getUuid());
//        }
//
//        // cached villagers
//        for (var entry : data.villagerStates.entrySet()) {
//            UUID uuid = entry.getKey();
//            NationData.VillagerSnapshot snapshot = entry.getValue();
//
//            if (processed.contains(uuid)) continue;
//            if (!snapshot.nation().equalsIgnoreCase(nation)) continue;
//
//            Ideology current = snapshot.ideology();
//            int happiness = snapshot.happiness();
//            Ideology updated = adjustIdeology(current, electedParty.getIdeology(), oldIdeology, happiness);
//            if (!current.equals(updated)) adjusted++;
//
//            data.villagerStates.put(uuid, new NationData.VillagerSnapshot(snapshot.nation(), happiness, updated));
//        }
//
//        int finalAdjusted = adjusted;
//        source.sendFeedback(() -> Text.literal("Adjusted ideology of " + finalAdjusted + " villagers in '" + nation + "'."), true);

        spawnIdeologyFirework(source, electedParty.getIdeology());
        return 1;
    }

    private static void spawnIdeologyFirework(ServerCommandSource source, Ideology ideology) {
        var world = source.getWorld();
        var player = source.getPlayer();
        if (player == null) return;

        var stack = createFireworkForIdeology(ideology);
        var firework = new FireworkRocketEntity(world, player.getX(), player.getY() + 1, player.getZ(), stack);
        world.spawnEntity(firework);
    }

    private static ItemStack createFireworkForIdeology(Ideology ideology) {
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        NbtCompound fireworks = new NbtCompound();
        NbtList explosions = new NbtList();
        NbtCompound explosion = new NbtCompound();

        explosion.putByte("Type", (byte) 1);
        explosion.putIntArray("Colors", new int[]{ideology.color});
        explosion.putBoolean("Flicker", true);
        explosion.putBoolean("Trail", true);

        explosions.add(explosion);

        NbtCompound fireworksTag = new NbtCompound();
        fireworksTag.putByte("Flight", (byte) 1);
        fireworksTag.put("Explosions", explosions);

        fireworks.put("Fireworks", fireworksTag);
        stack.setNbt(fireworks);
        return stack;
    }

    private static int unregisterNation(ServerCommandSource source, String name) {
        ServerWorld world = source.getWorld();
        NationData data = NationData.get(world);

        if (!data.contains(name)) {
            source.sendError(Text.literal("Nation '" + name + "' is not registered."));
            return 0;
        }

        data.removeNation(name);
        source.sendFeedback(() -> Text.literal("Removed nation: " + name), true);
        return 1;
    }

    private static int getNations(ServerCommandSource source) {
        ServerWorld world = source.getWorld();
        NationData data = NationData.get(world);

        if (data.getNations().isEmpty()) {
            source.sendFeedback(() -> Text.literal("No nations registered."), false);
            return 1;
        }

        String list = String.join(", ", data.getNations());
        source.sendFeedback(() -> Text.literal("Registered nations: " + list), false);
        return 1;
    }

    public static int registerNation(ServerCommandSource source, String name) {
        ServerWorld world = source.getWorld();
        NationData data = NationData.get(world);

        if (data.contains(name)) {
            source.sendError(Text.literal("Nation '" + name + "' is already registered."));
            return 0;
        }

        data.addNation(name);
        source.sendFeedback(() -> Text.literal("Registered nation: " + name), true);
        return 1;
    }

    private static int renameItemInHand(ServerCommandSource source, String newName) {
        ServerPlayerEntity player = source.getPlayer();
        ItemStack stack = player.getMainHandStack();

        if (stack.isEmpty()) {
            source.sendError(Text.literal("You are not holding any item."));
            return 0;
        }

        stack.setCustomName(Text.literal(newName));
        source.sendFeedback(() -> Text.literal("Renamed item in hand to: " + newName), true);
        return 1;
    }

    private static int listIdeologies(ServerCommandSource source) {
        ServerWorld world = source.getWorld();
        NationData data = NationData.get(world);

        // Start counts at 0 for all ideologies
        Map<Ideology, Long> counts = new EnumMap<>(Ideology.class);
        for (Ideology ideology : Ideology.values()) {
            counts.put(ideology, 0L);
        }

        // 1. Count live villagers in this world
        for (Entity entity : world.iterateEntities()) {
            if (entity instanceof VillagerEntity villager) {
                VillagerExt ext = (VillagerExt) villager;
                counts.merge(ext.getViewpoint().getIdeology(), 1L, Long::sum);
            }
        }

        // 2. Count cached villagers not currently loaded
        for (var entry : data.villagerStates.entrySet()) {
            UUID uuid = entry.getKey();
            NationData.VillagerSnapshot snapshot = entry.getValue();

            if (world.getEntity(uuid) != null) continue; // skip if loaded
            counts.merge(snapshot.ideology(), 1L, Long::sum);
        }

        // Build output
        StringBuilder sb = new StringBuilder("Ideologies & Support (all villagers):\n");
        for (Ideology ideology : Ideology.values()) {
            sb.append("• ").append(ideology.name()).append(": ").append(counts.get(ideology)).append("\n");
        }

        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        return 1;
    }

}
