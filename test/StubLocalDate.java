package test;

import java.time.LocalDate;
import java.util.function.Supplier;

public class StubLocalDate implements Supplier<LocalDate> {
  private LocalDate testDate;

  public StubLocalDate(LocalDate date) {
    this.testDate = date;
  }

  public LocalDate get() {
    return this.testDate;
  }

  public void set(LocalDate date) {
    this.testDate = date;
  }
}