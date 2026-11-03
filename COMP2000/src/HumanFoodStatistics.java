import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Collector-based analytics over the humans and food in a world. Kept
 * separate from {@link World} / {@code WorldStatistics} (the team's own
 * observer-driven reporting) so this stays a self-contained demonstration of
 * the Week 11 Collectors covered in the study guide: groupingBy,
 * partitioningBy, summarizingDouble, toMap, and downstream collectors.
 */
public final class HumanFoodStatistics {

    private HumanFoodStatistics() {
    }

    /** How many humans are currently sheltering vs out in the open. */
    public static Map<Boolean, Long> countByShelterStatus(List<Human> humans) {
        return humans.stream()
                .collect(Collectors.partitioningBy(Human::isHidden, Collectors.counting()));
    }

    /** Min / max / average / total energy across all humans, in one pass. */
    public static DoubleSummaryStatistics summarizeEnergy(List<Human> humans) {
        return humans.stream()
                .collect(Collectors.summarizingDouble(Human::getEnergy));
    }

    /** How many food items of each type are currently active, e.g. {APPLE=3, PIZZA=1}. */
    public static Map<FoodType, Long> countByFoodType(List<Food> food) {
        return food.stream()
                .collect(Collectors.groupingBy(Food::getType, Collectors.counting()));
    }

    /** Total energy value available, grouped by food type. */
    public static Map<FoodType, Double> totalEnergyByFoodType(List<Food> food) {
        return food.stream()
                .collect(Collectors.groupingBy(Food::getType,
                        Collectors.summingDouble(Food::getEnergyValue)));
    }

    /**
     * One entry per food-selection strategy class name to the humans using
     * it, e.g. {"NearestFoodStrategy"=[...], "HighestValueFoodStrategy"=[...]}.
     * Uses the mapping downstream collector to keep only names, not whole
     * Human objects, in each group.
     */
    public static Map<String, List<String>> strategyAssignments(List<Human> humans) {
        return humans.stream()
                .collect(Collectors.groupingBy(
                        human -> human.getFoodStrategy().getClass().getSimpleName(),
                        Collectors.mapping(human -> Integer.toHexString(human.hashCode()),
                                Collectors.toList())));
    }
}
