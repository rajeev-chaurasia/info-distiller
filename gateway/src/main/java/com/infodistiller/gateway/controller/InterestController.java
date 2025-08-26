package com.infodistiller.gateway.controller;

import com.infodistiller.gateway.repository.InterestRepository;
import com.infodistiller.gateway.dtos.InterestDTO;
import com.infodistiller.gateway.entity.Interest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interests")
public class InterestController {

    private final InterestRepository interestRepository;

    @Autowired
    public InterestController(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;
    }

    @PostMapping
    public Interest createInterest(@RequestBody InterestDTO interestDTO) {
        Interest newInterest = new Interest();
        newInterest.setUserId(1);
        newInterest.setName(interestDTO.getName());
        newInterest.setQueryTemplate(interestDTO.getQueryTemplate());
        return interestRepository.save(newInterest);
    }

    @GetMapping
    public List<Interest> getAllInterests() {
        return interestRepository.findByUserId(1);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Interest> getInterestById(@PathVariable Integer id) {
        return interestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Interest> updateInterest(@PathVariable Integer id, @RequestBody InterestDTO interestDTO) {
        return interestRepository.findById(id)
                .map(interest -> {
                    interest.setName(interestDTO.getName());
                    interest.setQueryTemplate(interestDTO.getQueryTemplate());
                    return ResponseEntity.ok(interestRepository.save(interest));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterest(@PathVariable Integer id) {
        return interestRepository.findById(id)
                .map(interest -> {
                    interestRepository.delete(interest);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}