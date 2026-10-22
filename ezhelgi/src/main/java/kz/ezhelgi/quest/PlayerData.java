package kz.ezhelgi.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/** Данные игрока, которые переживают смерть (петроглифы, задания аксакала, тоғыз құмалақ). */
public final class PlayerData {
    private PlayerData() {}

    private static final String PERSISTED = "PlayerPersisted";
    private static final String KEY = "ezhelgi";

    public static CompoundTag get(Player player) {
        CompoundTag root = player.getPersistentData();
        CompoundTag persisted = root.getCompound(PERSISTED).orElse(null);
        if (persisted == null) {
            persisted = new CompoundTag();
            root.put(PERSISTED, persisted);
        }
        CompoundTag mine = persisted.getCompound(KEY).orElse(null);
        if (mine == null) {
            mine = new CompoundTag();
            persisted.put(KEY, mine);
        }
        return mine;
    }
}
