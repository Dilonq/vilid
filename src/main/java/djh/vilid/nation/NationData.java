package djh.vilid.nation;

import djh.vilid.ideology.politics.Ideology;
import djh.vilid.ideology.politics.Party;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.*;
import java.util.stream.Collectors;

public class NationData extends PersistentState {

    public record VillagerSnapshot(String nation, int happiness, Ideology ideology) {}

    private final Set<String> nations = new HashSet<>();
    private final Map<String, List<Party>> nationParties = new HashMap<>();
    private final Map<String, String> rulingParty = new HashMap<>();
    public final Map<UUID, VillagerSnapshot> villagerStates = new HashMap<>();

    private static final PersistentState.Type<NationData> TYPE = new PersistentState.Type<>(
            NationData::new,
            NationData::fromNbt,
            null
    );

    public static NationData get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(TYPE, "nations");
    }

    // 1.21 Fix: Added RegistryWrapper.WrapperLookup
    public static NationData fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NationData data = new NationData();

        NbtList nationsList = nbt.getList("nations", NbtElement.STRING_TYPE);
        for (int i = 0; i < nationsList.size(); i++) {
            String name = nationsList.getString(i);
            data.nations.add(name);
        }

        NbtList partiesList = nbt.getList("nationParties", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < partiesList.size(); i++) {
            NbtCompound nationTag = partiesList.getCompound(i);
            String nation = nationTag.getString("nation");

            List<Party> parties = new ArrayList<>();
            NbtList partyList = nationTag.getList("parties", NbtElement.COMPOUND_TYPE);

            for (int j = 0; j < partyList.size(); j++) {
                NbtCompound partyTag = partyList.getCompound(j);
                String partyName = partyTag.getString("name");
                String ideologyStr = partyTag.getString("ideology");
                int prestige = partyTag.getInt("prestige");

                Party party = new Party(partyName, Ideology.valueOf(ideologyStr), prestige);
                parties.add(party);
            }

            data.nationParties.put(nation, parties);
        }

        NbtCompound rulingTag = nbt.getCompound("rulingParty");
        for (String nation : rulingTag.getKeys()) {
            data.rulingParty.put(nation, rulingTag.getString(nation));
        }

        if (nbt.contains("villagerStates", NbtElement.LIST_TYPE)) {
            NbtList villagerList = nbt.getList("villagerStates", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < villagerList.size(); i++) {
                NbtCompound villagerTag = villagerList.getCompound(i);
                UUID uuid = villagerTag.getUuid("uuid");
                String nation = villagerTag.getString("nation");
                int happiness = villagerTag.getInt("happiness");
                Ideology ideology = Ideology.valueOf(villagerTag.getString("ideology"));
                data.villagerStates.put(uuid, new VillagerSnapshot(nation, happiness, ideology));
            }
        }

        return data;
    }

    // 1.21 Fix: Added RegistryWrapper.WrapperLookup
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nationsList = new NbtList();
        for (String nation : nations) {
            nationsList.add(NbtString.of(nation));
        }
        nbt.put("nations", nationsList);

        NbtList partiesList = new NbtList();
        for (Map.Entry<String, List<Party>> entry : nationParties.entrySet()) {
            String nation = entry.getKey();
            List<Party> parties = entry.getValue();

            NbtCompound nationTag = new NbtCompound();
            nationTag.putString("nation", nation);

            NbtList partyList = new NbtList();
            for (Party party : parties) {
                NbtCompound partyTag = new NbtCompound();
                partyTag.putString("name", party.getName());
                partyTag.putString("ideology", party.getIdeology().name());
                partyTag.putInt("prestige", party.getPrestige());
                partyList.add(partyTag);
            }

            nationTag.put("parties", partyList);
            partiesList.add(nationTag);
        }
        nbt.put("nationParties", partiesList);

        NbtCompound rulingTag = new NbtCompound();
        for (Map.Entry<String, String> entry : rulingParty.entrySet()) {
            rulingTag.putString(entry.getKey(), entry.getValue());
        }
        nbt.put("rulingParty", rulingTag);

        NbtList villagerList = new NbtList();
        for (Map.Entry<UUID, VillagerSnapshot> entry : villagerStates.entrySet()) {
            NbtCompound villagerTag = new NbtCompound();
            villagerTag.putUuid("uuid", entry.getKey());
            VillagerSnapshot snap = entry.getValue();
            villagerTag.putString("nation", snap.nation());
            villagerTag.putInt("happiness", snap.happiness());
            villagerTag.putString("ideology", snap.ideology().name());
            villagerList.add(villagerTag);
        }
        nbt.put("villagerStates", villagerList);

        return nbt;
    }

    /**
     * Computes the median happiness of villagers in the given nation.
     * @param nation Nation name (case-insensitive)
     * @return OptionalDouble: empty if no villagers in the nation
     */
    public OptionalDouble getMedianHappiness(String nation) {
        int[] values = villagerStates.values().stream()
                .filter(v -> v.nation().equalsIgnoreCase(nation))
                .mapToInt(VillagerSnapshot::happiness)
                .sorted()
                .toArray();

        if (values.length == 0) return OptionalDouble.empty();

        int mid = values.length / 2;

        if (values.length % 2 == 0) {
            return OptionalDouble.of((values[mid - 1] + values[mid]) / 2.0);
        } else {
            return OptionalDouble.of(values[mid]);
        }
    }

    public void updateVillager(UUID uuid, String nation, int happiness, Ideology ideology) {
        villagerStates.put(uuid, new VillagerSnapshot(nation, happiness, ideology));
        markDirty();
    }

    public void removeVillager(UUID uuid) {
        villagerStates.remove(uuid);
        markDirty();
    }

    public double getAverageHappiness(String nation) {
        return villagerStates.values().stream()
                .filter(v -> v.nation().equalsIgnoreCase(nation)) // 1.21 Fix: Record getter
                .mapToInt(VillagerSnapshot::happiness)
                .average().orElse(0);
    }

    public Map<Ideology, Long> getIdeologyCounts(String nation) {
        return villagerStates.values().stream()
                .filter(v -> v.nation().equalsIgnoreCase(nation)) // 1.21 Fix: Record getter
                .collect(Collectors.groupingBy(VillagerSnapshot::ideology, Collectors.counting()));
    }

    public void setRulingParty(String nation, String party) {
        rulingParty.put(nation, party);
        markDirty();
    }

    public String getRulingParty(String nation) {
        return rulingParty.getOrDefault(nation, "(none)");
    }

    public void addNation(String name) {
        nations.add(name);
        markDirty();
    }

    public void removeNation(String name) {
        nations.remove(name);
        nationParties.remove(name);
        rulingParty.remove(name);
        markDirty();
    }

    public boolean addParty(String nation, Party party) {
        String nationKey = nation;
        List<Party> parties = nationParties.computeIfAbsent(nationKey, k -> new ArrayList<>());

        boolean exists = parties.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(party.getName()));

        if (exists) return false;

        parties.add(party);
        markDirty();
        return true;
    }

    public boolean removeParty(String nation, String partyName) {
        List<Party> parties = nationParties.get(nation);
        if (parties != null) {
            boolean removed = parties.removeIf(p -> p.getName().equalsIgnoreCase(partyName));
            if (removed) markDirty();
            return removed;
        }
        return false;
    }

    public List<Party> getParties(String nation) {
        return nationParties.getOrDefault(nation, List.of());
    }

    public boolean contains(String name) {
        return nations.contains(name);
    }

    public Set<String> getNations() {
        return nations;
    }
}