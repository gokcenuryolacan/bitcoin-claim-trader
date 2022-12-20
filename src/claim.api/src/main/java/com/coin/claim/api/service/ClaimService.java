package com.coin.claim.api.service;

import com.coin.claim.api.domain.Claim;
import com.coin.claim.api.model.Coin;
import com.coin.claim.api.model.GetActiveClaimResponseModel;
import com.coin.claim.api.model.GetActiveClaimV2ResponseModel;
import com.coin.claim.api.repository.ClaimRepository;
import lombok.AllArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;

    public String create(Claim claim) throws Exception {

        //region business validation

        if (!isDayAvailableInMonth(claim.ClaimDate)) {
            throw new Exception("You can not create claim between outside of 3 and 26 days of month.");
        }

        var totalPrice = claim.Coin.SalesPrice * claim.Coin.Amount;
        if (!isTotalPriceAvailable(totalPrice)) {
            throw new Exception("Total Price amount should be minimum 200 and maximum 30000.");
        }

        var existClaimByUser = claimRepository.findByUserId(claim.UserId);
        if (existClaimByUser != null) {

            throw new Exception("You can create claim maximum of 1.");
        }
        //endregion

        //region insert claim
        claim.Coin.TotalPrice = totalPrice;
        var insertedClaim = claimRepository.save(claim);
        //endregion

        //region publish ClaimCreated event to rabbitmq
        //endregion

        return insertedClaim.getId();
    }

    public String[] detailNotificationChannels(String UserId) {
        var detailClaim = claimRepository.findByUserId(UserId);
        return detailClaim.getNotificationChannels();
    }

    public String delete(String UserId) throws Exception {
        var existClaimByUser = claimRepository.findByUserId(UserId);
        if (existClaimByUser == null) {
            throw new Exception("You have already an active claim.");
        }
        var deletedClaim = claimRepository.deleteByUserId(UserId);

        return deletedClaim.getId();
    }

    public GetActiveClaimResponseModel activeClaim(String UserId) {
        var activeClaim = claimRepository.findByUserId(UserId);

        var responseModel = new GetActiveClaimResponseModel();
        responseModel.Date = activeClaim.ClaimDate;
        responseModel.Coin = new Coin();
        responseModel.Coin.Name = activeClaim.Coin.Name;
        responseModel.Coin.TotalPrice = activeClaim.Coin.TotalPrice;

        return responseModel;
    }

    public GetActiveClaimV2ResponseModel activeClaimForV2(String UserId) {
        var activeClaim = claimRepository.findByUserId(UserId);

        var responseModel = new GetActiveClaimV2ResponseModel();
        responseModel.NotificationChannels = activeClaim.NotificationChannels;
        responseModel.Coin = new Coin();
        responseModel.Coin.Name = activeClaim.Coin.Name;
        responseModel.Coin.TotalPrice = activeClaim.Coin.TotalPrice;

        return responseModel;
    }

    private boolean isTotalPriceAvailable(double totalPrice) {
        final double MIN_AVAILABLE_CLAIM_PRICE = 200;
        final double MAX_AVAILABLE_CLAIM_PRICE = 30000;
        if (totalPrice < MIN_AVAILABLE_CLAIM_PRICE || totalPrice > MAX_AVAILABLE_CLAIM_PRICE) {
            return false;
        }

        return true;
    }

    private boolean isDayAvailableInMonth(String ClaimDate) {
        var format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        var claimDateAsDateFormat = LocalDate.parse(ClaimDate, format);
        final int MIN_AVAILABLE_CLAIM_DAY = 3;
        final int MAX_AVAILABLE_CLAIM_DAY = 26;
        if (claimDateAsDateFormat.getDayOfMonth() <= MIN_AVAILABLE_CLAIM_DAY || claimDateAsDateFormat.getDayOfMonth() >= MAX_AVAILABLE_CLAIM_DAY) {
            return false;
        }
        return true;
    }
}
