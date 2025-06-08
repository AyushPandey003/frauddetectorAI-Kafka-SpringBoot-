package frauddetector.enums;  
  
import java.util.List;
import java.util.Random;
import java.util.Arrays;

public enum Category {  
    RETAIL, TECH, GROCERY,
    SHOPPING,
    ELECTRONICS,
    FOOD,
    BEVERAGES,
    TRANSPORTATION,
    FUEL,
    ENTERTAINMENT,
    SUBSCRIPTION;

    private static final Random RANDOM = new Random();
    private static final List<Category> ALL_CATEGORIES = Arrays.asList(values());

    public static Category getFrequentCategory(List<Category> userCategories) {  
        if (userCategories == null || userCategories.isEmpty()) {
            return ALL_CATEGORIES.get(RANDOM.nextInt(ALL_CATEGORIES.size()));
        }
        return userCategories.get(RANDOM.nextInt(userCategories.size()));  
    }

    public static Category getUnfrequentCategory(List<Category> userCategories) {   
        if (userCategories == null || userCategories.isEmpty()) {
            return ALL_CATEGORIES.get(RANDOM.nextInt(ALL_CATEGORIES.size()));
        }
        
        List<Category> infrequentCategories = ALL_CATEGORIES.stream()  
                .filter(category -> !userCategories.contains(category)) 
                .toList();  
        if (infrequentCategories.isEmpty()) {
            return getFrequentCategory(userCategories);
        }
        return infrequentCategories.get(RANDOM.nextInt(infrequentCategories.size()));  
    }
}