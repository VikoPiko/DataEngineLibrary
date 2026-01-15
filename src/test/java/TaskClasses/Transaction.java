package TaskClasses;

import com.viko.annotations.Column;
import com.viko.annotations.FileSource;
import com.viko.annotations.NotNull;
import com.viko.annotations.Range;

import java.time.LocalDate;

@FileSource(delimiter = "|")
public class Transaction {
    @NotNull
    @Column(index = 0, name = "transactionId")
    private String transactionId;

    @NotNull
    @Column(index = 1, name = "amount")
    @Range(min = 0.00) // no maximum amount specified, defaults to const MAX = 99999999.00
    public double amount;

    @NotNull
    @Column(index = 2, name = "timestamp")
    public LocalDate timestamp;
}
