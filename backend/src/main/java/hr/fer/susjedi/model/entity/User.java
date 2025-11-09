package hr.fer.susjedi.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String username;
    public String email;
    public String password;
    public String role;
}
