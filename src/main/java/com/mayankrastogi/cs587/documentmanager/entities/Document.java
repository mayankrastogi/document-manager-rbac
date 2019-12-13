package com.mayankrastogi.cs587.documentmanager.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    @ManyToOne(optional = false)
    private Label label;
    @Lob
    private String contents;

    public Document(String name, Label label, String contents) {
        this.name = name;
        this.label = label;
        this.contents = contents;
    }
}
