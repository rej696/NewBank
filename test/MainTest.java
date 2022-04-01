package test;

import newbank.server.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.function.Supplier;

public class MainTest {
    protected NewBank test = NewBank.getBank();
    protected test.StubLocalDate currentTime;
    protected Customer customer1;
    protected Customer customer2;
    protected CustomerID customerId1;
    protected CustomerID customerId2;


    public void setCurrentTime(LocalDate date) {
        currentTime.set(date);
        Loan.currentTime = (Supplier<LocalDate>) currentTime;
    }

    @BeforeEach
    public void setUp() {
        currentTime = new test.StubLocalDate(LocalDate.of(1996, 3, 6));
        Loan.currentTime = (Supplier<LocalDate>) currentTime;

        this.customer1 = new Customer();
        this.customer1 .addAccount(new Account("12345678", "Main", 1000));
        this.customerId1 = new CustomerID("TestID1");
        test.addCustomer(this.customer1, this.customerId1.getKey());

        this.customer2 = new Customer();
        this.customer2 .addAccount(new Account("23456789", "Current", 1000));
        this.customerId2 = new CustomerID("TestID2");
        test.addCustomer(this.customer2, this.customerId2.getKey());
    }

    @AfterEach
    public void tearDown() {
        test.clearLoans();
        test.clearCustomers();
    }
}