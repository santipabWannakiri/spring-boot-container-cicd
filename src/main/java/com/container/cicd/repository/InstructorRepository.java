package com.container.cicd.repository;



import com.container.cicd.model.Instructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("InstructorRepository")
public interface InstructorRepository extends CrudRepository<Instructor,Integer> {
}
