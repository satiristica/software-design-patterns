package bankrate;

import bankrate.commission.*;
import bankrate.bonus.*;
import bankrate.data.*;


public class App {

    public static void main(String[] args) {

    System.out.println();
    double amount = 17000;
    System.out.println("Amount: %.2f".formatted(amount));
    System.out.println();


    System.out.println("Manual commission selection: ");

    CommissionContext commission = new CommissionContext(new PercentageCommission());

    System.out.println("Percentage: " + commission.calculate(amount));

    commission.setStrategy(new FixedCommission());
    System.out.println("Fixed: " + commission.calculate(amount));


    commission.setStrategy(new ThresholdCommission());
    System.out.println("Threshold: " + commission.calculate(amount));
    System.out.println("");



    System.out.println("Manual bonus selection:");

    BonusProvider provider = new SponsorProvider();

    BonusContext bonus = new BonusContext(new FixedBonus());
    System.out.println("Fixed: " + bonus.calculate(amount));

    bonus.setStrategy(new StepBonus());
    System.out.println("Step: " + bonus.calculate(amount));

    bonus.setStrategy(new LargePurchaseBonus(provider));
    System.out.println("Large purchase: " + bonus.calculate(amount));
    System.out.println("");



    System.out.println("Automatic selection:");

    CustomerType type = CustomerType.BROKER; 

    CommissionSelector commissionSelector = new CommissionSelector();
    BonusSelector bonusSelector = new BonusSelector(provider);

    commission.setStrategy(commissionSelector.select(type));
    bonus.setStrategy(bonusSelector.select(type));

    System.out.println("Customer: " + type);
    System.out.println("Commission: " + commission.calculate(amount));
    System.out.println("Bonus: " + bonus.calculate(amount));
    


    System.out.println();
    System.out.println();
    bonus.setStrategy(new LargePurchaseBonus(provider));
    System.out.println("Large purchase: " + bonus.calculate(amount));
    System.out.println("");


    }
}