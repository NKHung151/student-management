package com.school.student_management.service.impl;

import com.school.student_management.dto.StudentCardDTO;
import com.school.student_management.entity.Student;
import com.school.student_management.entity.StudentCard;
import com.school.student_management.exception.ResourceNotFoundException;
import com.school.student_management.repository.StudentCardRepository;
import com.school.student_management.repository.StudentRepository;
import com.school.student_management.service.StudentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentCardServiceImpl implements StudentCardService {

    private final StudentCardRepository studentCardRepository;
    private final StudentRepository studentRepository;

    @Override
    public List<StudentCardDTO> getAllCards() {
        return studentCardRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public StudentCardDTO getCardById(Long id) {
        StudentCard card = studentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student card not found with id: " + id));
        return toDTO(card);
    }

    @Override
    @Transactional
    public StudentCardDTO createCard(StudentCardDTO dto) {
        if (dto.getStudentId() != null && studentCardRepository.findByStudentId(dto.getStudentId()).isPresent()) {
            throw new IllegalArgumentException("Student already has a card");
        }
        StudentCard card = toEntity(dto);
        return toDTO(studentCardRepository.save(card));
    }

    @Override
    @Transactional
    public StudentCardDTO updateCard(Long id, StudentCardDTO dto) {
        StudentCard card = studentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student card not found with id: " + id));
        
        card.setCardNumber(dto.getCardNumber());
        card.setIssueDate(dto.getIssueDate());
        card.setExpiryDate(dto.getExpiryDate());
        card.setStatus(dto.getStatus());

        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            card.setStudent(student);
        }

        return toDTO(studentCardRepository.save(card));
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        if (!studentCardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student card not found with id: " + id);
        }
        studentCardRepository.deleteById(id);
    }

    @Override
    public StudentCardDTO getCardByStudent(Long studentId) {
        StudentCard card = studentCardRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student card not found for student id: " + studentId));
        return toDTO(card);
    }

    private StudentCardDTO toDTO(StudentCard c) {
        StudentCardDTO dto = new StudentCardDTO();
        dto.setId(c.getId());
        dto.setCardNumber(c.getCardNumber());
        dto.setIssueDate(c.getIssueDate());
        dto.setExpiryDate(c.getExpiryDate());
        dto.setStatus(c.getStatus());
        if (c.getStudent() != null) {
            dto.setStudentId(c.getStudent().getId());
            dto.setStudentName(c.getStudent().getFullName());
            dto.setStudentCode(c.getStudent().getStudentCode());
        }
        return dto;
    }

    private StudentCard toEntity(StudentCardDTO dto) {
        StudentCard c = new StudentCard();
        c.setCardNumber(dto.getCardNumber());
        c.setIssueDate(dto.getIssueDate());
        c.setExpiryDate(dto.getExpiryDate());
        c.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        
        if (dto.getStudentId() != null) {
            studentRepository.findById(dto.getStudentId()).ifPresent(c::setStudent);
        }
        return c;
    }

    @Override
    public List<StudentCardDTO> getCardsByParent(String username) {
        return studentCardRepository.findByParentUsername(username).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
