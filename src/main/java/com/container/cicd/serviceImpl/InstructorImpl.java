package com.container.cicd.serviceImpl;


import com.container.cicd.model.Instructor;
import com.container.cicd.repository.InstructorRepository;
import com.container.cicd.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorImpl implements InstructorService {

    private InstructorRepository instructorRepository;

    @Autowired
    public InstructorImpl(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Override
    public Instructor save(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    @Override
    public Optional<Instructor> findById(int id) {
        return instructorRepository.findById(id);
    }

    @Override
    public List<Instructor> findAll() {
        return (List<Instructor>) instructorRepository.findAll();
    }

    @Override
    public void deleteById(int id) {
        instructorRepository.deleteById(id);
    }
}
