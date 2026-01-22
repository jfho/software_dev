/**
 * @author s215698
 */

package dtu.util;

import java.util.List;
import java.util.stream.Collectors;

import dtu.models.MerchantTransaction;
import dtu.models.RecordedPayment;

public class PaymentConverterMerchant {
   
    public static List<MerchantTransaction> toMerchantTransactions(
        List<RecordedPayment> recordedPayments
    ) {
        return recordedPayments.stream()
            .map(payment -> new MerchantTransaction(
                payment.tokenId,
                payment.merchantId,
                payment.amount
            ))
            .collect(Collectors.toList());
    }   


}
