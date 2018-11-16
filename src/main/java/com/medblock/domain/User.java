package com.medblock.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medblock.config.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A user.
 */

@org.springframework.data.mongodb.core.mapping.Document(collection = "jhi_user")
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @NotNull@Pattern(regexp = Constants.LOGIN_REGEX)@Size(min = 1, max = 50)
    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Indexed
    private String login;

    @NotNull@Size(min = 60, max = 60)
    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    private String password;

    @Size(max = 50)
    @Size(max = 50)
    @Field("first_name")
    private String firstName;

    @Size(max = 50)
    @Size(max = 50)
    @Field("last_name")
    private String lastName;

    @Email@Size(min = 5, max = 254)
    @Email
    @Size(min = 5, max = 254)
    @Indexed
    private String email;

    private boolean activated = false;

    @Size(min = 2, max = 6)
    @Size(min = 2, max = 6)
    @Field("lang_key")
    private String langKey;

    @Size(max = 256)
    @Size(max = 256)
    @Field("image_url")
    private String imageUrl;

    @Size(max = 20)
    @Size(max = 20)
    @Field("activation_key")
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Size(max = 20)
    @Field("reset_key")
    @JsonIgnore
    private String resetKey;

    @Field("reset_date")
    private Instant resetDate = null;

    @JsonIgnore
    private Set<Authority> authorities = new HashSet<>();

    @Field("phone")
    private String phone;

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        return !(user.getId() == null || getId() == null) && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public String getId() {
        return this.id;
    }

    public @NotNull @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) @NotNull @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) String getLogin() {
        return this.login;
    }

    public @NotNull @Size(min = 60, max = 60) @NotNull @Size(min = 60, max = 60) String getPassword() {
        return this.password;
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

    public boolean isActivated() {
        return this.activated;
    }

    public @Size(min = 2, max = 6) @Size(min = 2, max = 6) String getLangKey() {
        return this.langKey;
    }

    public @Size(max = 256) @Size(max = 256) String getImageUrl() {
        return this.imageUrl;
    }

    public @Size(max = 20) @Size(max = 20) String getActivationKey() {
        return this.activationKey;
    }

    public @Size(max = 20) @Size(max = 20) String getResetKey() {
        return this.resetKey;
    }

    public Instant getResetDate() {
        return this.resetDate;
    }

    public Set<Authority> getAuthorities() {
        return this.authorities;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLogin(@NotNull @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) @NotNull @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) String login) {
        this.login = login;
    }

    public void setPassword(@NotNull @Size(min = 60, max = 60) @NotNull @Size(min = 60, max = 60) String password) {
        this.password = password;
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

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void setLangKey(@Size(min = 2, max = 6) @Size(min = 2, max = 6) String langKey) {
        this.langKey = langKey;
    }

    public void setImageUrl(@Size(max = 256) @Size(max = 256) String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setActivationKey(@Size(max = 20) @Size(max = 20) String activationKey) {
        this.activationKey = activationKey;
    }

    public void setResetKey(@Size(max = 20) @Size(max = 20) String resetKey) {
        this.resetKey = resetKey;
    }

    public void setResetDate(Instant resetDate) {
        this.resetDate = resetDate;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String toString() {
        return "User(id=" + this.getId() + ", login=" + this.getLogin() + ", password=" + this.getPassword() + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName() + ", email=" + this.getEmail() + ", activated=" + this.isActivated() + ", langKey=" + this.getLangKey() + ", imageUrl=" + this.getImageUrl() + ", activationKey=" + this.getActivationKey() + ", resetKey=" + this.getResetKey() + ", resetDate=" + this.getResetDate() + ", authorities=" + this.getAuthorities() + ", phone=" + this.getPhone() + ")";
    }
}
