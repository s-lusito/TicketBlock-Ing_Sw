package com.ticketblock.dto.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseTicketRequest {
    @NotNull
    private HashMap<Integer, Boolean> ticketFeeMap;
    @NotNull
    @CreditCardNumber
    @Size(min = 12, max = 19)
    private String creditCardNumber;

    @NotNull
    @Pattern(
            regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", // MM/YY
            message = "Expiration must be in MM/YY format"
    )
    private String expirationDate;

    @NotNull
    @Size(min = 3, max = 4)
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be numeric")
    private String cvv;

    @NotNull
    @Size(min = 2, max = 50)
    private String cardHolderName;


}
