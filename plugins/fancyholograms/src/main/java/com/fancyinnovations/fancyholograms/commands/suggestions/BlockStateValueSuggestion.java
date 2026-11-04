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
        return switch (blockData) {
            case Directional directional when "facing".equals(property) -> {
                yield directional.getFaces().stream().map(Enum::name);
            }
            case Orientable orientable when "axis".equals(property) -> {
                yield orientable.getAxes().stream().map(Enum::name);
            }
            case Rotatable rotatable when "rotation".equals(property) -> {
                yield IntStream.rangeClosed(0, 15).mapToObj(Integer::toString);
            }
            case StructureBlock structureBlock when "mode".equals(property) -> {
                yield Arrays.stream(StructureBlock.Mode.values()).map(Enum::name);
            }
            case Stairs stairs when "shape".equals(property) -> {
                yield Arrays.stream(Stairs.Shape.values()).map(Enum::name);
            }
            case Slab slab when "type".equals(property) -> {
                yield Arrays.stream(Slab.Type.values()).map(Enum::name);
            }
            case Chest chest when "type".equals(property) -> {
                yield Arrays.stream(Chest.Type.values()).map(Enum::name);
            }
            case Door door when "hinge".equals(property) -> {
                yield Arrays.stream(Door.Hinge.values()).map(Enum::name);
            }
            case Bisected bisected when "half".equals(property) -> {
                yield Arrays.stream(Bisected.Half.values()).map(Enum::name);
            }
            case Bed bed when "part".equals(property) -> {
                yield Arrays.stream(Bed.Part.values()).map(Enum::name);
            }
            case Ageable ageable when "age".equals(property) -> {
                yield IntStream.rangeClosed(0, ageable.getMaximumAge()).mapToObj(Integer::toString);
            }
            case Levelled levelled when "level".equals(property) -> {
                yield IntStream.rangeClosed(0, levelled.getMaximumLevel()).mapToObj(Integer::toString);
            }
            case AnaloguePowerable ap when "power".equals(property) -> {
                yield IntStream.rangeClosed(0, ap.getMaximumPower()).mapToObj(Integer::toString);
            }
            case FaceAttachable faceAttachable when ("face".equals(property) || "attachment".equals(property)) -> {
                yield Arrays.stream(FaceAttachable.AttachedFace.values()).map(Enum::name);
            }
            case Bell bell when "attachment".equals(property) -> {
                yield Arrays.stream(Bell.Attachment.values()).map(Enum::name);
            }
            case RedstoneWire rw -> {
                if ("north".equals(property) || "south".equals(property) || "east".equals(property) || "west".equals(property)) {
                    yield Arrays.stream(RedstoneWire.Connection.values()).map(Enum::name);
                }
                if ("power".equals(property)) {
                    yield IntStream.rangeClosed(0, rw.getMaximumPower()).mapToObj(Integer::toString);
                }
                yield Stream.empty();
            }
            case Snow snow when "layers".equals(property) -> {
                yield IntStream.rangeClosed(1, snow.getMaximumLayers()).mapToObj(Integer::toString);
            }
            case Cake cake when "bites".equals(property) -> {
                yield IntStream.rangeClosed(0, cake.getMaximumBites()).mapToObj(Integer::toString);
            }
            case TechnicalPiston technicalPiston when "type".equals(property) -> {
                yield Arrays.stream(TechnicalPiston.Type.values()).map(Enum::name);
            }
            case Bamboo bamboo -> {
                if ("leaves".equals(property)) {
                    yield Arrays.stream(Bamboo.Leaves.values()).map(Enum::name);
                }
                if ("age".equals(property)) {
                    yield IntStream.rangeClosed(0, bamboo.getMaximumAge()).mapToObj(Integer::toString);
                }
                if ("stage".equals(property)) {
                    yield Stream.of("0", "1");
                }
                yield Stream.empty();
            }
            case NoteBlock noteBlock -> {
                if ("instrument".equals(property)) {
                    yield Arrays.stream(Instrument.values()).map(Enum::name);
                }
                if ("note".equals(property)) {
                    yield IntStream.rangeClosed(0, 24).mapToObj(Integer::toString);
                }
                yield Stream.empty();
            }
            case Rail rail when "shape".equals(property) -> {
                yield Arrays.stream(Rail.Shape.values()).map(Enum::name);
            }
            case null, default -> {
                yield Stream.empty();
            }
        };
    }
}
