public final class HumanFoodTest {

    private HumanFoodTest() {
    }

    public static void main(String[] args) {
        testEnergyDecreasesAfterUpdate();
        testInvalidFoodEnergyIsRejected();
        testEatingIncreasesEnergyAndConsumesFood();
        testDistantFoodCannotBeEaten();
        testFoodExpiresAfter300Updates();
        testHumanEntersAndLeavesBuilding();
        testStarvationDeactivatesHuman();
        testSimulationAssetsLoadWithoutSourceClasspath();
        testFoodIsEdible();
        testFoodConstructorOverloadUsesDefaultEnergy();
        testFoodStrategiesPickDifferentFood();
        testHumanFoodStatisticsCollectors();
        System.out.println("HumanFoodTest passed");
    }

    /** Week 11 collectors: groupingBy, partitioningBy, summarizingDouble, mapping. */
    private static void testHumanFoodStatisticsCollectors() {
        Human sheltered = new Human(120, 140);
        Human outInTheOpen = new Human(500, 500);
        Building building = new Building(100, 100, 40, 40, 1);
        assert sheltered.enterBuilding(building);
        java.util.List<Human> humans = java.util.List.of(sheltered, outInTheOpen);

        java.util.Map<Boolean, Long> byShelter = HumanFoodStatistics.countByShelterStatus(humans);
        assert byShelter.get(true) == 1;
        assert byShelter.get(false) == 1;

        java.util.DoubleSummaryStatistics energyStats =
                HumanFoodStatistics.summarizeEnergy(humans);
        assert energyStats.getCount() == 2;

        Food apple = new Food(0, 0, 10);
        Food sameTypeApple = new Food(0, 0, 15);
        java.util.List<Food> food = java.util.List.of(apple, sameTypeApple);
        java.util.Map<FoodType, Long> byType = HumanFoodStatistics.countByFoodType(food);
        assert byType.get(apple.getType()) == 2;

        java.util.Map<FoodType, Double> energyByType =
                HumanFoodStatistics.totalEnergyByFoodType(food);
        assert energyByType.get(apple.getType()) == 25.0;
    }

    /**
     * Strategy pattern: swapping a human's FoodSelectionStrategy at runtime
     * changes which food it picks, without changing Human itself - mirrors
     * the course's RubberDuck.setFlyBehavior() example.
     */
    private static void testFoodStrategiesPickDifferentFood() {
        World world = new World(1000, 1000);
        Human human = new Human(100, 100);
        Food closerLowValue = new Food(110, 100, 5);
        Food fartherHighValue = new Food(200, 100, 90);
        world.addEntity(human);
        world.addEntity(closerLowValue);
        world.addEntity(fartherHighValue);

        assert new NearestFoodStrategy().selectFood(world, human, 250).get() == closerLowValue;
        assert new HighestValueFoodStrategy().selectFood(world, human, 250).get()
                == fartherHighValue;

        human.setFoodStrategy(new HighestValueFoodStrategy());
        assert human.getFoodStrategy().selectFood(world, human, 250).get() == fartherHighValue;
    }

    /** The two-argument constructor overload should default to DEFAULT_ENERGY_VALUE. */
    private static void testFoodConstructorOverloadUsesDefaultEnergy() {
        Food defaultEnergyFood = new Food(10, 10);
        Food customEnergyFood = new Food(10, 10, 99);

        assert defaultEnergyFood.getEnergyValue() == Food.DEFAULT_ENERGY_VALUE;
        assert customEnergyFood.getEnergyValue() == 99;
    }

    /** Food implements the Edible contract independently of its Entity behaviour. */
    private static void testFoodIsEdible() {
        Edible food = new Food(0, 0, 20);

        assert food.getEnergyValue() == 20;
        assert !food.isConsumed();
        assert food.describeEnergy().contains("20.0");

        food.consume();
        assert food.isConsumed();
        assert food.describeEnergy().equals("already consumed");
    }

    private static void testEnergyDecreasesAfterUpdate() {
        Human human = new Human(500, 500);
        double initialEnergy = human.getEnergy();

        human.update(new World(1000, 1000));

        assert human.getEnergy() < initialEnergy;
    }

    private static void testInvalidFoodEnergyIsRejected() {
        boolean rejected = false;
        try {
            new Food(0, 0, 0);
        } catch (IllegalArgumentException exception) {
            rejected = true;
        }

        assert rejected;
    }

    private static void testEatingIncreasesEnergyAndConsumesFood() {
        Human human = new Human(100, 100);
        human.update(new World(1000, 1000));
        double energyBeforeEating = human.getEnergy();
        Food food = new Food(100, 100, 20);

        assert human.eat(food);
        assert human.getEnergy() > energyBeforeEating;
        assert !food.isActive();
    }

    private static void testDistantFoodCannotBeEaten() {
        Human human = new Human(100, 100);
        Food food = new Food(200, 100, 20);

        assert !human.eat(food);
        assert food.isActive();
    }

    private static void testFoodExpiresAfter300Updates() {
        Food food = new Food(0, 0, 20);
        World world = new World(1000, 1000);

        for (int tick = 0; tick < 299; tick++) {
            food.update(world);
        }
        assert food.isActive();

        food.update(world);
        assert !food.isActive();
    }

    private static void testHumanEntersAndLeavesBuilding() {
        Human human = new Human(120, 140);
        Building building = new Building(100, 100, 40, 40, 1);

        assert human.enterBuilding(building);
        assert human.isHidden();
        assert human.getShelter() == building;
        assert building.getOccupantCount() == 1;

        human.leaveBuilding();
        assert !human.isHidden();
        assert human.getShelter() == null;
        assert building.getOccupantCount() == 0;
        assert human.getX() == building.getEntranceX();
        assert human.getY() == building.getEntranceY();
    }

    private static void testStarvationDeactivatesHuman() {
        Human human = new Human(500, 500);
        World world = new World(1000, 1000);

        for (int tick = 0; tick < 600 && human.isActive(); tick++) {
            human.update(world);
        }

        assert !human.isActive();
    }

    private static void testSimulationAssetsLoadWithoutSourceClasspath() {
        World world = new World(1000, 750);

        new Building(155, 125, 150, 100);
        new SimPanel(world);
    }
}
