package com.medblock.service.dto;

import com.medblock.config.Constants;
import com.medblock.domain.Authority;
import com.medblock.domain.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

    private String id;

    @NotBlank@Pattern(regexp = Constants.LOGIN_REGEX)@Size(min = 1, max = 50)
    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    @Size(max = 50)
    private String lastName;

    @Email@Size(min = 5, max = 254)
    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(max = 256)
    @Size(max = 256)
    private String imageUrl;

    private boolean activated = false;

    @Size(min = 2, max = 6)
    @Size(min = 2, max = 6)
    private String langKey;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Set<String> authorities;

    private String phone;

    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.activated = user.isActivated();
        this.imageUrl = user.getImageUrl();
        this.langKey = user.getLangKey();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.authorities = user.getAuthorities().stream()
            .map(Authority::getName)
            .collect(Collectors.toSet());
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UserDTO;
    }

    public String getId() {
        return this.id;
    }

    public @NotBlank @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) @NotBlank @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) String getLogin() {
        return this.login;
    }

    public @Size(max = 50) @Size(max = 50) String getFirstName() {
        return this.firstName;
    }

    public @Size(max = 50) @Size(max = 50) String getLastName() {
        return this.lastName;
    }

    public @Email @Size(min = 5, max = 254) @Email @Size(min = 5, max = 254) String getEmail() {
        return this.email;
    }

    public @Size(max = 256) @Size(max = 256) String getImageUrl() {
        return this.imageUrl;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public @Size(min = 2, max = 6) @Size(min = 2, max = 6) String getLangKey() {
        return this.langKey;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Set<String> getAuthorities() {
        return this.authorities;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLogin(@NotBlank @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) @NotBlank @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) String login) {
        this.login = login;
    }

    public void setFirstName(@Size(max = 50) @Size(max = 50) String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(@Size(max = 50) @Size(max = 50) String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(@Email @Size(min = 5, max = 254) @Email @Size(min = 5, max = 254) String email) {
        this.email = email;
    }

    public void setImageUrl(@Size(max = 256) @Size(max = 256) String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void setLangKey(@Size(min = 2, max = 6) @Size(min = 2, max = 6) String langKey) {
        this.langKey = langKey;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof UserDTO)) return false;
        final UserDTO other = (UserDTO) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$login = this.getLogin();
        final Object other$login = other.getLogin();
        if (this$login == null ? other$login != null : !this$login.equals(other$login)) return false;
        final Object this$firstName = this.getFirstName();
        final Object other$firstName = other.getFirstName();
        if (this$firstName == null ? other$firstName != null : !this$firstName.equals(other$firstName)) return false;
        final Object this$lastName = this.getLastName();
        final Object other$lastName = other.getLastName();
        if (this$lastName == null ? other$lastName != null : !this$lastName.equals(other$lastName)) return false;
        final Object this$email = this.getEmail();
        final Object other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
        final Object this$imageUrl = this.getImageUrl();
        final Object other$imageUrl = other.getImageUrl();
        if (this$imageUrl == null ? other$imageUrl != null : !this$imageUrl.equals(other$imageUrl)) return false;
        if (this.isActivated() != other.isActivated()) return false;
        final Object this$langKey = this.getLangKey();
        final Object other$langKey = other.getLangKey();
        if (this$langKey == null ? other$langKey != null : !this$langKey.equals(other$langKey)) return false;
        final Object this$createdBy = this.getCreatedBy();
        final Object other$createdBy = other.getCreatedBy();
        if (this$createdBy == null ? other$createdBy != null : !this$createdBy.equals(other$createdBy)) return false;
        final Object this$createdDate = this.getCreatedDate();
        final Object other$createdDate = other.getCreatedDate();
        if (this$createdDate == null ? other$createdDate != null : !this$createdDate.equals(other$createdDate))
            return false;
        final Object this$lastModifiedBy = this.getLastModifiedBy();
        final Object other$lastModifiedBy = other.getLastModifiedBy();
        if (this$lastModifiedBy == null ? other$lastModifiedBy != null : !this$lastModifiedBy.equals(other$lastModifiedBy))
            return false;
        final Object this$lastModifiedDate = this.getLastModifiedDate();
        final Object other$lastModifiedDate = other.getLastModifiedDate();
        if (this$lastModifiedDate == null ? other$lastModifiedDate != null : !this$lastModifiedDate.equals(other$lastModifiedDate))
            return false;
        final Object this$authorities = this.getAuthorities();
        final Object other$authorities = other.getAuthorities();
        if (this$authorities == null ? other$authorities != null : !this$authorities.equals(other$authorities))
            return false;
        final Object this$phone = this.getPhone();
        final Object other$phone = other.getPhone();
        if (this$phone == null ? other$phone != null : !this$phone.equals(other$phone)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $login = this.getLogin();
        result = result * PRIME + ($login == null ? 43 : $login.hashCode());
        final Object $firstName = this.getFirstName();
        result = result * PRIME + ($firstName == null ? 43 : $firstName.hashCode());
        final Object $lastName = this.getLastName();
        result = result * PRIME + ($lastName == null ? 43 : $lastName.hashCode());
        final Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final Object $imageUrl = this.getImageUrl();
        result = result * PRIME + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        result = result * PRIME + (this.isActivated() ? 79 : 97);
        final Object $langKey = this.getLangKey();
        result = result * PRIME + ($langKey == null ? 43 : $langKey.hashCode());
        final Object $createdBy = this.getCreatedBy();
        result = result * PRIME + ($createdBy == null ? 43 : $createdBy.hashCode());
        final Object $createdDate = this.getCreatedDate();
        result = result * PRIME + ($createdDate == null ? 43 : $createdDate.hashCode());
        final Object $lastModifiedBy = this.getLastModifiedBy();
        result = result * PRIME + ($lastModifiedBy == null ? 43 : $lastModifiedBy.hashCode());
        final Object $lastModifiedDate = this.getLastModifiedDate();
        result = result * PRIME + ($lastModifiedDate == null ? 43 : $lastModifiedDate.hashCode());
        final Object $authorities = this.getAuthorities();
        result = result * PRIME + ($authorities == null ? 43 : $authorities.hashCode());
        final Object $phone = this.getPhone();
        result = result * PRIME + ($phone == null ? 43 : $phone.hashCode());
        return result;
    }

    public String toString() {
        return "UserDTO(id=" + this.getId() + ", login=" + this.getLogin() + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName() + ", email=" + this.getEmail() + ", imageUrl=" + this.getImageUrl() + ", activated=" + this.isActivated() + ", langKey=" + this.getLangKey() + ", createdBy=" + this.getCreatedBy() + ", createdDate=" + this.getCreatedDate() + ", lastModifiedBy=" + this.getLastModifiedBy() + ", lastModifiedDate=" + this.getLastModifiedDate() + ", authorities=" + this.getAuthorities() + ", phone=" + this.getPhone() + ")";
    }
}
