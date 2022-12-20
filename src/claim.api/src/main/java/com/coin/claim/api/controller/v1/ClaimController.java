package com.coin.claim.api.controller.v1;

import com.coin.claim.api.domain.Claim;
import com.coin.claim.api.model.GetActiveClaimResponseModel;
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
@RequestMapping("api/v1/claims")
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping(path = "create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> CreateClaim(@RequestBody Claim claim) throws Exception {

        var responseMessage = "";

        try {
            var createdClaimId = claimService.create(claim);
            responseMessage = createdClaimId + " claim number has been received.";
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    @GetMapping("")
    public ResponseEntity<GetActiveClaimResponseModel> GetActiveClaim(@RequestParam String userId) {
        var claim = claimService.activeClaim(userId);
        return ResponseEntity.status(HttpStatus.OK).body(claim);
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> DeleteClaim(@RequestParam String userId) throws Exception{
        try {
            var deletedClaimId = claimService.delete(userId);
           return ResponseEntity.status(HttpStatus.OK).body(deletedClaimId + " claim number has been cancelled.");
        } catch (Exception e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("notifications")
    public String[] GetNotificationsType(@RequestParam String userId) { return claimService.detailNotificationChannels(userId); }
}
