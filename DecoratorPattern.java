/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Pattern;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author poornimaepy
 */
interface Coffee {
    String getDescription();
    double getCost();
}

class SimpleCoffee implements Coffee {
    public String getDescription() {
        return "Simple coffee";
    }

    public double getCost() {
        return 5;
    }
}

class MilkDecorator implements Coffee {
    protected Coffee decoratedCoffee;

    public MilkDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }

    public String getDescription() {
        return decoratedCoffee.getDescription() + ", milk";
    }

    public double getCost() {
        return decoratedCoffee.getCost() + 1.5;
    }
}

class SugarDecorator implements Coffee {
    protected Coffee decoratedCoffee;

    public SugarDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }

    public String getDescription() {
        return decoratedCoffee.getDescription() + ", sugar";
    }

    public double getCost() {
        return decoratedCoffee.getCost() + 0.5;
    }
}

public class DecoratorPattern {
    public static void main(String[] args) {
        Coffee coffee = new SimpleCoffee();
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());

        coffee = new MilkDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());

        coffee = new SugarDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
    }
}

