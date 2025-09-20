package sit.int204.itbmsbackend.controller.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.config.FileStorageProperties;
import sit.int204.itbmsbackend.dto.common.ApiResponse;
import sit.int204.itbmsbackend.dto.user.UpdateUserRequest;
import sit.int204.itbmsbackend.dto.user.UserProfileResponse;
import sit.int204.itbmsbackend.entity.SaleItemImage;
import sit.int204.itbmsbackend.entity.User;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.service.ImageStorageService;
import sit.int204.itbmsbackend.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ImageStorageService imageService;
    private final FileStorageProperties properties;

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

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getUserImage(@PathVariable String filename) throws IOException {
        Resource resourceFile = imageService.getImage(filename);

        Path filePath = Paths.get(properties.getUploadDir()).resolve(filename).normalize();
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resourceFile);
    }
}
