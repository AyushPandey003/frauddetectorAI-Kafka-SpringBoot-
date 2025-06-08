package frauddetector.enums;  
  
import java.util.List;
import java.util.Random;

public enum Currency {  
    EUR, USD, GBP;  

    public static Currency getRandomSuspiciousCurrency(Currency preferredCurrency) {  
        List<Currency> allCurrencies = List.of(Currency.values());  

        List<Currency> infrequentCurrencies = allCurrencies.stream()  
                .filter(currency -> !currency.equals(preferredCurrency))  
                .toList();  

        if (infrequentCurrencies.isEmpty()) {  
            return preferredCurrency;  
        }  

        Random random = new Random();  

        return infrequentCurrencies.get(random.nextInt(infrequentCurrencies.size()));  
    }
}