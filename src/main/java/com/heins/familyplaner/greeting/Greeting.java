package com.heins.familyplaner.greeting;

import jakarta.persistence.*;


@Entity
@Table(name="greetings")
public class Greeting{

    public Greeting() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;



    public Long getId() { return id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}