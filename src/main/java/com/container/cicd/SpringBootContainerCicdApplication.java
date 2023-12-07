package com.container.cicd;

import com.container.cicd.model.Instructor;
import com.container.cicd.model.InstructorDetail;
import com.container.cicd.service.InstructorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootContainerCicdApplication {
    private InstructorService instructorService;

    public SpringBootContainerCicdApplication(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootContainerCicdApplication.class, args);
    }

    @Bean
    public CommandLineRunner initialInstructor(String[] args) {
        return r -> {
            createInstructorObject();
        };
    }

    private void createInstructorObject() {
        InstructorDetail instructorDetailA = new InstructorDetail("https://youtube.com/somsak.s", "Cycling");
        Instructor instructorA = new Instructor("130990102277550", "SOMSAK", "SANDEE", "SOMSAK.S@gmail.com", instructorDetailA);

        InstructorDetail instructorDetailB = new InstructorDetail("https://youtube.com/somchai", "Singing");
        Instructor instructorB = new Instructor("140998822435608", "SOMCHAI", "RIT", "SOMCHAI.R@gmail.com", instructorDetailB);

        InstructorDetail instructorDetailC = new InstructorDetail("https://youtube.com/jirapon", "Tiktoker");
        Instructor instructorC = new Instructor("750993648223447", "JIRAPON", "KIM", "JIRAPON.K@gmail.com", instructorDetailC);

        instructorService.save(instructorA);
        instructorService.save(instructorB);
        instructorService.save(instructorC);
    }

}
