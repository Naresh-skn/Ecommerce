package com.springboot.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long roleId;

        @Enumerated(EnumType.STRING)
        private AppRole roleName;


}
