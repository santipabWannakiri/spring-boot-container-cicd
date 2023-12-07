package com.container.cicd.service;


import com.container.cicd.model.Instructor;

import java.util.List;
import java.util.Optional;

public interface InstructorService {

    public Instructor save(Instructor instructor);
    public Optional<Instructor> findById(int id);
    public List<Instructor> findAll();
    public void deleteById(int id);

}
