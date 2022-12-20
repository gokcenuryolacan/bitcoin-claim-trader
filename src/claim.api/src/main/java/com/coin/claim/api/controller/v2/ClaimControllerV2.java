package com.coin.claim.api.controller.v2;

import com.coin.claim.api.domain.Claim;
import com.coin.claim.api.model.GetActiveClaimResponseModel;
import com.coin.claim.api.model.GetActiveClaimV2ResponseModel;
import com.coin.claim.api.service.ClaimService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v2/claims")
public class ClaimControllerV2 {

    private final ClaimService claimService;

    @GetMapping("")
    public ResponseEntity<GetActiveClaimV2ResponseModel> GetActiveClaim(@RequestParam String userId) {
        var claim = claimService.activeClaimForV2(userId);
        return ResponseEntity.status(HttpStatus.OK).body(claim);
    }
}
