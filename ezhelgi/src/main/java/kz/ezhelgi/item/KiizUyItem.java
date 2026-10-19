package kz.ezhelgi.item;

import kz.ezhelgi.EzhelgiMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

/**
 * Набор юрты: ПКМ по земле ставит готовую юрту из шаблона
 * (data/ezhelgi/structure/yurt/*.nbt). Дверь смотрит на игрока.
 * Центр юрты — там, куда кликнули.
 */
public class KiizUyItem extends Item {
    private final String template;
    private final int radius;

    public KiizUyItem(Properties properties, String template, int radius) {
        super(properties);
        this.template = template;
        this.radius = radius;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos ground = context.getClickedPos();

        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }

        Optional<StructureTemplate> found = server.getServer().getStructureTemplateManager()
                .get(Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "yurt/" + this.template));
        if (found.isEmpty()) {
            EzhelgiMod.LOGGER.error("Yurt template {} not found", this.template);
            return InteractionResult.FAIL;
        }

        if (!hasRoom(level, ground)) {
            if (player != null) {
                player.sendSystemMessage(Component.translatable("message.ezhelgi.kiiz_uy.no_room"));
            }
            return InteractionResult.FAIL;
        }

        StructureTemplate yurt = found.get();
        Vec3i size = yurt.getSize();
        int c = size.getX() / 2;

        // Шаблон построен дверью на юг (+Z); поворачиваем дверью к игроку
        Direction door = context.getHorizontalDirection().getOpposite();
        Rotation rotation = switch (door) {
            case WEST -> Rotation.CLOCKWISE_90;
            case NORTH -> Rotation.CLOCKWISE_180;
            case EAST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
        int rx;
        int rz;
        switch (rotation) {
            case CLOCKWISE_90 -> { rx = -c; rz = c; }
            case CLOCKWISE_180 -> { rx = -c; rz = -c; }
            case COUNTERCLOCKWISE_90 -> { rx = c; rz = -c; }
            default -> { rx = c; rz = c; }
        }
        BlockPos origin = ground.offset(-rx, 0, -rz);

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setIgnoreEntities(true);
        yurt.placeInWorld(server, origin, origin, settings, server.getRandom(), 2);

        level.playSound(null, ground, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f);
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    /** Над площадкой должно быть почти пусто: трава и цветы не мешают. */
    private boolean hasRoom(Level level, BlockPos ground) {
        int blocked = 0;
        int r = this.radius;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                if (dx * dx + dz * dz > r * r) continue;
                for (int dy = 1; dy <= r; dy++) {
                    if (!level.getBlockState(ground.offset(dx, dy, dz)).canBeReplaced()) {
                        blocked++;
                    }
                }
            }
        }
        return blocked <= r * 3;
    }
}
