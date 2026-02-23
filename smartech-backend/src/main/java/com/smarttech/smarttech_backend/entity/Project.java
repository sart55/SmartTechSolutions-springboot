package com.smarttech.smarttech_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String customerName;
    private String description;
    private String customerEmail;
    private String customerContact;
    private String customerCollege;
    private String customerBranch;

    @Column(nullable = false)
    private Boolean quotationCreated = false;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Quotation> quotations;

    // SIMPLE STRING STATUS
    private String status = "OPEN";


}