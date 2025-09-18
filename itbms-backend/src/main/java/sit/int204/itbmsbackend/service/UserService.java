package sit.int204.itbmsbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.constant.UserType;
import sit.int204.itbmsbackend.dto.user.UpdateUserRequest;
import sit.int204.itbmsbackend.dto.user.UserProfileResponse;
import sit.int204.itbmsbackend.entity.User;
import sit.int204.itbmsbackend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    private void validateSellerField(UpdateUserRequest seller) {
        if (seller.getShopName() == null ||
                seller.getPhone() == null ||
                seller.getBankAccountNumber() == null ||
                seller.getBankName() == null ||
                seller.getIdCardNumber() == null
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid seller fields.");
        }
    }

    public UserProfileResponse findById(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        UserProfileResponse response = new UserProfileResponse();
        response.setUserType(user.getUserType());
        response.setId(user.getId());
        response.setNickname(user.getNickname());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());

        response.setShopName(user.getShopName());
        response.setPhone(user.getPhone());
        response.setBankAccountNumber(user.getBankAccountNumber());
        response.setBankName(user.getBankName());
        response.setIdCardNumber(user.getIdCardNumber());
        response.setIdCardImageFront(user.getIdCardImageFront());
        response.setIdCardImageBack(user.getIdCardImageBack());
        response.setIsLocked(user.getIsLocked());

        response.setStatus(user.getStatus());
        response.setCreatedOn(user.getCreatedOn());
        response.setUpdatedOn(user.getUpdatedOn());
        response.setAddresses(user.getAddresses());
        response.setRoles(user.getRolesStr());

        return response;
    }

    public void updateUserProfile(Integer userId, UpdateUserRequest updateUser) {
        User existingUser = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        existingUser.setNickname(updateUser.getNickname());
        existingUser.setFullName(updateUser.getFullName());
        existingUser.setUserType(updateUser.getUserType());
        if (UserType.SELLER.equals(updateUser.getUserType())) {
            // Validate additional seller field
            validateSellerField(updateUser);

            // Validate upload image and save to image storage
            String idCardImageFrontFileName;
            String idCardImageBackFileName;
            // ID Card Front
            if (updateUser.getKeptIdCardImageFront()) {
                idCardImageFrontFileName = existingUser.getIdCardImageFront();
            } else {
                if (existingUser.getIdCardImageFront() != null) {
                    imageStorageService.deleteImage(existingUser.getIdCardImageFront());
                }
                idCardImageFrontFileName = updateUser.getIdCardImageFront() != null
                        ? imageStorageService.saveImage(updateUser.getIdCardImageFront())
                        : null;
            }
            // ID Card Back
            if (updateUser.getKeptIdCardImageBack()) {
                idCardImageBackFileName = existingUser.getIdCardImageBack();
            } else {
                if (existingUser.getIdCardImageBack() != null) {
                    imageStorageService.deleteImage(existingUser.getIdCardImageBack());
                }
                idCardImageBackFileName = updateUser.getIdCardImageBack() != null
                        ? imageStorageService.saveImage(updateUser.getIdCardImageBack())
                        : null;
            }

            // Adding additional field of seller
            existingUser.setPhone(updateUser.getPhone());
            existingUser.setShopName(updateUser.getShopName());
            existingUser.setBankName(updateUser.getBankName());
            existingUser.setBankAccountNumber(updateUser.getBankAccountNumber());
            existingUser.setIdCardNumber(updateUser.getIdCardNumber());
            existingUser.setIdCardImageFront(idCardImageFrontFileName);
            existingUser.setIdCardImageBack(idCardImageBackFileName);
        }

        userRepository.save(existingUser);
    }
}
