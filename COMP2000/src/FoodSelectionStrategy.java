import java.util.Optional;

/**
 * Strategy for choosing which nearby food a human goes for.
 *
 * <p>Different humans can be given different strategies, and a human's
 * strategy can be swapped at runtime without changing {@link Human} itself
 * - the Strategy pattern, applied the same way the course's FlyBehavior
 * example separates "a duck" from "how a duck flies".
 */
public interface FoodSelectionStrategy {

    /**
     * Picks a food item (if any) within {@code searchRadius} of {@code human}.
     * Returns {@link Optional#empty()} rather than throwing when nothing
     * suitable is in range, leaving the caller to decide what "no food" means.
     */
    Optional<Food> selectFood(World world, Human human, double searchRadius);
}
