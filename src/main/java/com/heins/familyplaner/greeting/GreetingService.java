package com.heins.familyplaner.greeting;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GreetingService {

    private final GreetingRepository greetingRepository;

    public GreetingService(GreetingRepository greetingRepository){
        this.greetingRepository = greetingRepository;
    }

    public List<GreetingDto> getAllGreetings(){
        return greetingRepository.findAll()
                .stream()
                .map(g -> new GreetingDto(g.getId(), g.getMessage()))
                .toList();
    }

    public GreetingDto createGreeting(String message)
    {
        Greeting greeting = new Greeting();
        greeting.setMessage(message);
        Greeting saved = greetingRepository.save(greeting);
        return new GreetingDto(saved.getId(), saved.getMessage());
    }
}
