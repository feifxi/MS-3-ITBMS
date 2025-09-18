package sit.int204.itbmsbackend.controller.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dto.common.ApiResponse;
import sit.int204.itbmsbackend.dto.user.UpdateUserRequest;
import sit.int204.itbmsbackend.dto.user.UserProfileResponse;
import sit.int204.itbmsbackend.entity.User;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.service.UserService;

@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User loggedInUser = userPrincipal.getUser();
        if (!loggedInUser.getRolesStr().contains("ADMIN") && !loggedInUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        UserProfileResponse userDetailsResponse = userService.findById(id);
        return ResponseEntity.ok(userDetailsResponse);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse> updateProfile(
            @PathVariable Integer id,
            @ModelAttribute UpdateUserRequest updateUserRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User loggedInUser = userPrincipal.getUser();
        if (!loggedInUser.getRolesStr().contains("ADMIN") && !loggedInUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        userService.updateUserProfile(id, updateUserRequest);
        return ResponseEntity.ok(new ApiResponse(true, "User profile updated successfully!"));
    }
}
