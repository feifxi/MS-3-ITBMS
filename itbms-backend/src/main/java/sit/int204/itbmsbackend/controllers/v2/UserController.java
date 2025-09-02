package sit.int204.itbmsbackend.controllers.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dtos.auth.UserDetailsResponse;
import sit.int204.itbmsbackend.dtos.common.ApiResponse;
import sit.int204.itbmsbackend.dtos.user.UpdateUserRequest;
import sit.int204.itbmsbackend.dtos.user.UserProfileResponse;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.services.UserService;

@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in.");
        }
        UserProfileResponse userDetailsResponse = userService.getUserProfile(userPrincipal.getUser());
        return ResponseEntity.ok(userDetailsResponse);
    }

    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    @PreAuthorize("hasRole('USER') and #userId == principal.id")
    public ResponseEntity<ApiResponse> updateProfile(
//            @PathVariable Integer userId,
            @ModelAttribute UpdateUserRequest updateUserRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        userService.updateUserProfile(userPrincipal.getUser(), updateUserRequest);
        return ResponseEntity.ok(new ApiResponse(true, "User profile updated successfully!"));
    }
}
