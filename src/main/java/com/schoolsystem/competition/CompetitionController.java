package com.schoolsystem.competition;

import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.student.ServiceStudent;
import com.schoolsystem.teacher.ServiceTeacher;
import com.schoolsystem.user.EntityUser;
import com.schoolsystem.user.EnumUserType;
import com.schoolsystem.user.ServiceUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin()
public class CompetitionController {

    private ModelMapper modelMapper;
    private ServiceUser serviceUser;
    private ServiceStudent serviceStudent;
    private ServiceTeacher serviceTeacher;
    private ServiceCompetitionParticipation serviceCompetitionParticipation;
    private ServiceCompetition serviceCompetition;

    public CompetitionController(ServiceUser serviceUser, ServiceCompetition serviceCompetition, ModelMapper modelMapper, ServiceStudent serviceStudent, ServiceTeacher serviceTeacher, ServiceCompetitionParticipation serviceCompetitionParticipation) {
        this.serviceUser = serviceUser;
        this.serviceCompetition = serviceCompetition;
        this.modelMapper = modelMapper;
        this.serviceStudent = serviceStudent;
        this.serviceTeacher = serviceTeacher;
        this.serviceCompetitionParticipation = serviceCompetitionParticipation;
    }

    @PostMapping("/competition")
    @ApiOperation(value = "Add new competition to database, admin or teacher only",
            notes = "admin or teacher only operation",
            response = CompetitionGetDTO.class)
    public ResponseEntity<?> addCompetition(@Valid @RequestBody CompetitionPostDTO competitionPostDTO) {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if ((currentUser.getUserType() == EnumUserType.TEACHER || currentUser.getUserType() == EnumUserType.ADMIN) && !competitionPostDTO.getName().isEmpty()) {
            EntityCompetition competition = new EntityCompetition();
            competition.setDescription(competitionPostDTO.getDescription());
            competition.setName(competitionPostDTO.getName());
            return ResponseEntity.ok(modelMapper.map(serviceCompetition.save(competition), CompetitionGetDTO.class));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/competition/{competitionId}")
    @ApiOperation(value = "Change name/description of given competiton by ID",
            notes = "Admin and teacher only operation",
            response = CompetitionGetDTO.class)
    public ResponseEntity<?> editCompetition(@Valid @PathVariable Long competitionId, @Valid @RequestBody CompetitionPostDTO competitionNewValues) {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        Optional<EntityCompetition> optionalEntityCompetition = serviceCompetition.get(competitionId);
        return optionalEntityCompetition.map(entityCompetition -> {
            if ((currentUser.getUserType() == EnumUserType.TEACHER || currentUser.getUserType() == EnumUserType.ADMIN) && !competitionNewValues.getName().isEmpty()) {
                entityCompetition.setName(competitionNewValues.getName());
                entityCompetition.setDescription(competitionNewValues.getDescription());
                serviceCompetition.update(entityCompetition);
                return ResponseEntity.ok(modelMapper.map(serviceCompetition.save(entityCompetition), CompetitionGetDTO.class));
            } else {
                return ResponseEntity.ok().build();
            }
        }).orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/competition")
    @ApiOperation(value = "Returns all available competitions",
            notes = "Operation for every authenticated user",
            response = CompetitionGetDTO.class,
            responseContainer = "List")
    public ResponseEntity<List<CompetitionGetDTO>> getCompetitions() {
        return ResponseEntity.ok(modelMapper.map(serviceCompetition.getAll(), new TypeToken<List<CompetitionGetDTO>>() {
        }.getType()));
    }

    @PostMapping("/competitionParticipation")
    @ApiOperation(value = "Assign student to competition",
            notes = "Operation for teacher only!")
    public ResponseEntity<?> addStudentToCompetition(@RequestBody @Valid CompetitionParticipationPostDTO competitionParticipation) {
        Optional<EntityStudent> student = serviceStudent.get(competitionParticipation.getStudentId());
        EntityUser teacherUser = serviceUser.getCurrentUserFromToken().get();
        Optional<EntityCompetition> competition = serviceCompetition.get(competitionParticipation.getCompetitionId());
        //if all data is correct then add new assignment
        if (teacherUser.getUserType() == EnumUserType.TEACHER && student.isPresent() && competition.isPresent()) {
            EntityCompetitionParticipation entityCompetitionParticipation = new EntityCompetitionParticipation();
            entityCompetitionParticipation.setCompetition(competition.get());
            entityCompetitionParticipation.setStudent(student.get());
            entityCompetitionParticipation.setTeacher(teacherUser.getEntityTeacher());
            entityCompetitionParticipation.setDescription(competitionParticipation.getDescription());
            serviceCompetitionParticipation.save(entityCompetitionParticipation);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/competitionParticipation/{competitionParticipationId}")
    @ApiOperation(value = "Deassign student from competition",
            notes = "Operation for teacher only!")
    public ResponseEntity<?> deleteStudentFromCompetition(@Valid @PathVariable Long competitionParticipationId) {
        EntityUser teacherUser = serviceUser.getCurrentUserFromToken().get();
        Optional<EntityCompetitionParticipation> competitionParticipation = serviceCompetitionParticipation.get(competitionParticipationId);
        if (teacherUser.getUserType() == EnumUserType.TEACHER && competitionParticipation.isPresent()) {
            serviceCompetitionParticipation.delete(competitionParticipation.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/competition/{competitionId}")
    @ApiOperation(value = "Delete competition record and all its competitionParticipation assignments.")
    public ResponseEntity<?> deleteCompetition(@Valid @PathVariable Long competitionId) {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if (currentUser.getUserType() != EnumUserType.ADMIN && currentUser.getUserType() != EnumUserType.TEACHER) {
            return ResponseEntity.badRequest().build();
        } else {
            Optional<EntityCompetition> competition = serviceCompetition.get(competitionId);
            if (competition.isPresent()) {
                List<EntityCompetitionParticipation> competitionParticipations = serviceCompetitionParticipation.findAllByCompetitionId(competitionId);
                competitionParticipations.forEach(e -> {
                    serviceCompetitionParticipation.delete(e);
                });
                serviceCompetition.delete(competition.get());
                return ResponseEntity.ok().build();
            }else{
                return ResponseEntity.badRequest().build();
            }

        }
    }

    @GetMapping("/competitionParticipation/{competitionId}")
    @ApiOperation(value = "Return all student-competition assignments for given competition",
            notes = "Operation for authenticated user",
            response = CompetitionParticipationGetDTO.class,
            responseContainer = "List")
    public ResponseEntity<List<CompetitionParticipationGetDTO>> getStudentsCompetition(@Valid @PathVariable Long competitionId) {
        return ResponseEntity.ok(convertCompetitionParticipationToDTOList(serviceCompetitionParticipation.findAllByCompetitionId(competitionId)));
    }

    private List<CompetitionParticipationGetDTO> convertCompetitionParticipationToDTOList(List<EntityCompetitionParticipation> competitionParticipations) {
        List<CompetitionParticipationGetDTO> dtos = new ArrayList<>();
        competitionParticipations.forEach(competitionParticipation -> {
            CompetitionParticipationGetDTO dto = new CompetitionParticipationGetDTO();
            dto.setCompetitionId(competitionParticipation.getCompetition().getId());
            dto.setCompetitionName(competitionParticipation.getCompetition().getName());
            dto.setDescriptionCompetition(competitionParticipation.getCompetition().getDescription());
            dto.setDescriptionParticipation(competitionParticipation.getDescription());
            dto.setId(competitionParticipation.getId());
            dto.setStudentFirstName(competitionParticipation.getStudent().getUsers().getFirstName());
            dto.setStudentLastName(competitionParticipation.getStudent().getUsers().getLastName());
            dto.setStudentId(competitionParticipation.getStudent().getId());
            dtos.add(dto);
        });
        return dtos;
    }

    @GetMapping("/competitionParticipation")
    @ApiOperation(value = "Return all student-competition assignments",
            notes = "Operation for authenticated user",
            response = CompetitionParticipationGetDTO.class,
            responseContainer = "List")
    public ResponseEntity<List<CompetitionParticipationGetDTO>> getStudentsCompetition() {
        return ResponseEntity.ok(convertCompetitionParticipationToDTOList(serviceCompetitionParticipation.getAll()));
    }


    @GetMapping("/competitionParticipationTeacher/{teacherId}")
    @ApiOperation(value = "Return all student-competition assignments added by teacher",
            notes = "Operation for authenticated user",
            response = CompetitionParticipationGetDTO.class,
            responseContainer = "List")
    public ResponseEntity<?> getStudentsCompetitionTeacher(@Valid @PathVariable Long teacherId) {
        return serviceTeacher.get(teacherId).map(entityTeacher -> {
            return ResponseEntity.ok(convertCompetitionParticipationToDTOList(serviceCompetitionParticipation.findAllByTeacher(entityTeacher)));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/competitionParticipationStudent/{studentId}")
    @ApiOperation(value = "Return all student-competition assignments, of given student",
            notes = "Operation for authenticated user",
            response = CompetitionParticipationGetDTO.class,
            responseContainer = "List")
    public ResponseEntity<List<CompetitionParticipationGetDTO>> getStudentsCompetitionStudent(@Valid @PathVariable Long studentId) {
        return serviceStudent.get(studentId).map(student -> {
            return ResponseEntity.ok(convertCompetitionParticipationToDTOList(serviceCompetitionParticipation.findAllByStudent(student)));
        }).orElse(ResponseEntity.badRequest().build());
    }
}
