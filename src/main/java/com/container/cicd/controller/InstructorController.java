package com.container.cicd.controller;


import com.container.cicd.model.Instructor;
import com.container.cicd.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
public class InstructorController {
    private InstructorService instructorService;
    @Autowired
    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/instructor")
    public ResponseEntity<?> getInstructorById(@RequestParam("id") int id){
        Optional<Instructor> instructorOptional = instructorService.findById(id);

        if (instructorOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(instructorOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("[]");
        }
    }

    @GetMapping("/instructors")
    public ResponseEntity<?> getAllInstructor(){
        List<Instructor> listResult = instructorService.findAll();
        if(listResult.size() != 0 ){
            return ResponseEntity.status(HttpStatus.OK).body(listResult);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("[]");
        }
    }

    @PostMapping("/add-instructor")
    public ResponseEntity<?> addNewInstructor(@RequestBody Instructor instructor){
        Instructor Result = instructorService.save(instructor);
        if(Result != null && Result.getId() >= 0){
            return ResponseEntity.status(HttpStatus.OK).body(Result);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("[]");
        }

    }
}
