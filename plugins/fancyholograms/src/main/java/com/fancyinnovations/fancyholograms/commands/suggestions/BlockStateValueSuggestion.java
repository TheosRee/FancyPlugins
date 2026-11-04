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
import java.util.stream.Stream;

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

        return parseProperty(blockData, property).map(s -> s.toLowerCase(Locale.ROOT)).toList();
    }

    private Stream<String> parseProperty(BlockData blockData, String property) {
        if (blockData instanceof Directional directional && "facing".equals(property)) {
            return directional.getFaces().stream().map(Enum::name);
        }
        if (blockData instanceof Orientable orientable && "axis".equals(property)) {
            return orientable.getAxes().stream().map(Enum::name);
        }
        if (blockData instanceof Rotatable && "rotation".equals(property)) {
            return IntStream.rangeClosed(0, 15).mapToObj(Integer::toString);
        }
        if (blockData instanceof StructureBlock && "mode".equals(property)) {
            return Arrays.stream(StructureBlock.Mode.values()).map(Enum::name);
        }
        if (blockData instanceof Stairs && "shape".equals(property)) {
            return Arrays.stream(Stairs.Shape.values()).map(Enum::name);
        }
        if (blockData instanceof Slab && "type".equals(property)) {
            return Arrays.stream(Slab.Type.values()).map(Enum::name);
        }
        if (blockData instanceof Chest && "type".equals(property)) {
            return Arrays.stream(Chest.Type.values()).map(Enum::name);
        }
        if (blockData instanceof Door && "hinge".equals(property)) {
            return Arrays.stream(Door.Hinge.values()).map(Enum::name);
        }
        if (blockData instanceof Bisected && "half".equals(property)) {
            return Arrays.stream(Bisected.Half.values()).map(Enum::name);
        }
        if (blockData instanceof Bed && "part".equals(property)) {
            return Arrays.stream(Bed.Part.values()).map(Enum::name);
        }
        if (blockData instanceof Ageable ageable && "age".equals(property)) {
            return IntStream.rangeClosed(0, ageable.getMaximumAge()).mapToObj(Integer::toString);
        }
        if (blockData instanceof Levelled levelled && "level".equals(property)) {
            return IntStream.rangeClosed(0, levelled.getMaximumLevel()).mapToObj(Integer::toString);
        }
        if (blockData instanceof AnaloguePowerable ap && "power".equals(property)) {
            return IntStream.rangeClosed(0, ap.getMaximumPower()).mapToObj(Integer::toString);
        }
        if (blockData instanceof FaceAttachable && ("face".equals(property) || "attachment".equals(property))) {
            return Arrays.stream(FaceAttachable.AttachedFace.values()).map(Enum::name);
        }
        if (blockData instanceof Bell && "attachment".equals(property)) {
            return Arrays.stream(Bell.Attachment.values()).map(Enum::name);
        }
        if (blockData instanceof RedstoneWire rw) {
            if ("north".equals(property) || "south".equals(property) || "east".equals(property) || "west".equals(property)) {
                return Arrays.stream(RedstoneWire.Connection.values()).map(Enum::name);
            }
            if ("power".equals(property)) {
                return IntStream.rangeClosed(0, rw.getMaximumPower()).mapToObj(Integer::toString);
            }
            return Stream.empty();
        }
        if (blockData instanceof Snow snow && "layers".equals(property)) {
            return IntStream.rangeClosed(1, snow.getMaximumLayers()).mapToObj(Integer::toString);
        }
        if (blockData instanceof Cake cake && "bites".equals(property)) {
            return IntStream.rangeClosed(0, cake.getMaximumBites()).mapToObj(Integer::toString);
        }
        if (blockData instanceof TechnicalPiston && "type".equals(property)) {
            return Arrays.stream(TechnicalPiston.Type.values()).map(Enum::name);
        }
        if (blockData instanceof Bamboo bamboo) {
            if ("leaves".equals(property)) {
                return Arrays.stream(Bamboo.Leaves.values()).map(Enum::name);
            }
            if ("age".equals(property)) {
                return IntStream.rangeClosed(0, bamboo.getMaximumAge()).mapToObj(Integer::toString);
            }
            if ("stage".equals(property)) {
                return Stream.of("0", "1");
            }
            return Stream.empty();
        }
        if (blockData instanceof NoteBlock) {
            if ("instrument".equals(property)) {
                return Arrays.stream(Instrument.values()).map(Enum::name);
            }
            if ("note".equals(property)) {
                return IntStream.rangeClosed(0, 24).mapToObj(Integer::toString);
            }
            return Stream.empty();
        }
        if (blockData instanceof Rail && "shape".equals(property)) {
            return Arrays.stream(Rail.Shape.values()).map(Enum::name);
        }

        return Stream.empty();
    }
}
