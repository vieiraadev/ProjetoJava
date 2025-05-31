package com.estudomais.demo.model;

import java.io.Serializable;

public class Disciplina implements Serializable {
    private String nome;
    private String codigo;
    private int cargaHoraria;
    private String semestre;
    private String professorResponsavel;

    public Disciplina(String nome, String codigo, int cargaHoraria, String semestre, String professorResponsavel) {
        this.nome = nome;
        this.codigo = codigo;
        this.cargaHoraria = cargaHoraria;
        this.semestre = semestre;
        this.professorResponsavel = professorResponsavel;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(int cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    public String getProfessorResponsavel() { return professorResponsavel; }
    public void setProfessorResponsavel(String professorResponsavel) { this.professorResponsavel = professorResponsavel; }
}
