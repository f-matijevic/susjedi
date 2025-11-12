package hr.fer.susjedi.model.request;

import hr.fer.susjedi.model.enums.UserRole;

public class RegisterRequest {
    public String Name;
    public String email;
    public String lozinka;
    public UserRole role;
}