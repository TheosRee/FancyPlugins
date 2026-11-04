package com.fancyinnovations.fancyholograms.commands.suggestions;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.bukkit.Instrument;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.*;
import java.util.stream.IntStream;

public class BlockStateValueSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        Hologram hologram = context.getResolvedArgumentOrNull(Hologram.class);
        if (hologram == null || !(hologram.getData() instanceof BlockHologramData blockHoloData)) {
            return List.of();
        }

        String rawProperty = context.getResolvedArgumentOrNull("property");
        if (rawProperty == null) {
            return List.of();
        }
        String property = rawProperty.toLowerCase(Locale.ROOT);

        BlockData blockData = blockHoloData.getBlockData();
        if (blockData == null) {
            return List.of();
        }

        Map<String, String> properties = blockHoloData.getBlockStateProperties();
        String currentValue = properties.get(property);
        if (currentValue != null && (currentValue.equalsIgnoreCase("true") || currentValue.equalsIgnoreCase("false"))) {
            return List.of("true", "false");
        }

        if (blockData instanceof Directional directional && "facing".equals(property)) {
            return directional.getFaces().stream().map(f -> f.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Orientable orientable && "axis".equals(property)) {
            return orientable.getAxes().stream().map(a -> a.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Rotatable && "rotation".equals(property)) {
            return IntStream.rangeClosed(0, 15).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof StructureBlock && "mode".equals(property)) {
            return Arrays.stream(StructureBlock.Mode.values()).map(m -> m.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Stairs && "shape".equals(property)) {
            return Arrays.stream(Stairs.Shape.values()).map(s -> s.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Slab && "type".equals(property)) {
            return Arrays.stream(Slab.Type.values()).map(t -> t.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Chest && "type".equals(property)) {
            return Arrays.stream(Chest.Type.values()).map(t -> t.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Door && "hinge".equals(property)) {
            return Arrays.stream(Door.Hinge.values()).map(h -> h.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Bisected && "half".equals(property)) {
            return Arrays.stream(Bisected.Half.values()).map(h -> h.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Bed && "part".equals(property)) {
            return Arrays.stream(Bed.Part.values()).map(p -> p.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Ageable ageable && "age".equals(property)) {
            return IntStream.rangeClosed(0, ageable.getMaximumAge()).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof Levelled levelled && "level".equals(property)) {
            return IntStream.rangeClosed(0, levelled.getMaximumLevel()).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof AnaloguePowerable ap && "power".equals(property)) {
            return IntStream.rangeClosed(0, ap.getMaximumPower()).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof FaceAttachable && ("face".equals(property) || "attachment".equals(property))) {
            return Arrays.stream(FaceAttachable.AttachedFace.values()).map(f -> f.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Bell && "attachment".equals(property)) {
            return Arrays.stream(Bell.Attachment.values()).map(a -> a.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof RedstoneWire rw) {
            if ("north".equals(property) || "south".equals(property) || "east".equals(property) || "west".equals(property)) {
                return Arrays.stream(RedstoneWire.Connection.values()).map(c -> c.name().toLowerCase(Locale.ROOT)).toList();
            }
            if ("power".equals(property)) {
                return IntStream.rangeClosed(0, rw.getMaximumPower()).mapToObj(Integer::toString).toList();
            }
        }
        if (blockData instanceof Snow snow && "layers".equals(property)) {
            return IntStream.rangeClosed(1, snow.getMaximumLayers()).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof Cake cake && "bites".equals(property)) {
            return IntStream.rangeClosed(0, cake.getMaximumBites()).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof TechnicalPiston && "type".equals(property)) {
            return Arrays.stream(TechnicalPiston.Type.values()).map(t -> t.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof Bamboo bamboo) {
            if ("leaves".equals(property)) {
                return Arrays.stream(Bamboo.Leaves.values()).map(l -> l.name().toLowerCase(Locale.ROOT)).toList();
            }
            if ("age".equals(property)) {
                return IntStream.rangeClosed(0, bamboo.getMaximumAge()).mapToObj(Integer::toString).toList();
            }
            if ("stage".equals(property)) {
                return List.of("0", "1");
            }
        }
        if (blockData instanceof NoteBlock && "instrument".equals(property)) {
            return Arrays.stream(Instrument.values()).map(i -> i.name().toLowerCase(Locale.ROOT)).toList();
        }
        if (blockData instanceof NoteBlock && "note".equals(property)) {
            return IntStream.rangeClosed(0, 24).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof Rail && "shape".equals(property)) {
            return Arrays.stream(Rail.Shape.values()).map(s -> s.name().toLowerCase(Locale.ROOT)).toList();
        }

        return List.of();
    }
}
