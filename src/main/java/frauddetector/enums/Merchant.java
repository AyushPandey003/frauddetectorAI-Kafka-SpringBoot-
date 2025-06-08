package frauddetector.enums;  
  
import java.util.List;  
import java.util.Map;  
import java.util.Random;  
import java.util.Map.Entry;  
import static java.util.Map.entry;  
  
public enum Merchant {  
   
    // Retail  
    AMAZON, WALMART, BEST_BUY, TARGET, COSTCO, ETSY, EBAY, IKEA,  

    // Tech  
    APPLE, MICROSOFT, GOOGLE, SAMSUNG, SONY, DELL, HP, LENOVO,  

    // Grocery  
    DUNNES_STORES, LIDL, TESCO, ALDI, WHOLE_FOODS, TRADER_JOES,  

    // Food & Beverages  
    STARBUCKS, MCDONALDS, BURGER_KING, SUBWAY, DOMINOS, PIZZA_HUT,  

    // Transportation & Fuel  
    SHELL, BP, EXXON, CHEVRON, TEXACO,  

    // Entertainment & Subscription  
    NETFLIX, SPOTIFY, DISNEY_PLUS, HBO_MAX, AMAZON_PRIME,  

    // Shopping  
    ZARA, H_M, NIKE, ADIDAS, GAP;  
  
    private static final Random RANDOM = new Random();  
  
    private static final Map<Category, List<Merchant>> CATEGORY_MERCHANTS = Map.ofEntries(  
            entry(Category.RETAIL, List.of(AMAZON, WALMART, BEST_BUY, TARGET, COSTCO, ETSY, EBAY, IKEA)),  
            entry(Category.TECH, List.of(APPLE, MICROSOFT, GOOGLE, SAMSUNG, SONY, DELL, HP, LENOVO)),  
            entry(Category.GROCERY, List.of(DUNNES_STORES, LIDL, TESCO, ALDI, WHOLE_FOODS, TRADER_JOES)),  
            entry(Category.FOOD, List.of(MCDONALDS, BURGER_KING, SUBWAY, DOMINOS, PIZZA_HUT)),  
            entry(Category.BEVERAGES, List.of(STARBUCKS)),  
            entry(Category.TRANSPORTATION, List.of(SHELL, BP, EXXON, CHEVRON, TEXACO)),  
            entry(Category.FUEL, List.of(SHELL, BP, EXXON, CHEVRON, TEXACO)),  
            entry(Category.ENTERTAINMENT, List.of(NETFLIX, SPOTIFY, DISNEY_PLUS, HBO_MAX, AMAZON_PRIME)),  
            entry(Category.SUBSCRIPTION, List.of(NETFLIX, SPOTIFY, DISNEY_PLUS, HBO_MAX, AMAZON_PRIME)),  
            entry(Category.SHOPPING, List.of(ZARA, H_M, NIKE, ADIDAS, GAP)),  
            entry(Category.ELECTRONICS, List.of(APPLE, MICROSOFT, GOOGLE, SAMSUNG, SONY, DELL, HP, LENOVO))  
    );  
  
    public static Merchant getRandomMerchant(Category category) {  
        List<Merchant> merchants = CATEGORY_MERCHANTS.get(category);  
        if (merchants == null || merchants.isEmpty()) {  
            // Fallback to all merchants if category not found  
            Merchant[] allMerchants = values();  
            return allMerchants[RANDOM.nextInt(allMerchants.length)];  
        }  
        return merchants.get(RANDOM.nextInt(merchants.size()));  
    }  
}