package com.heins.familyplaner.greeting;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("api/greetings")
public class GreetingController {
    private static final String template = "Hello %s!";
    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService){
        this.greetingService = greetingService;
    }

    @GetMapping
    public List<GreetingDto> getAllGreetings(){
        return greetingService.getAllGreetings();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GreetingDto createGreeting(@RequestParam(defaultValue = "World") String name){
        return greetingService.createGreeting(template.formatted(name));
    }
}
