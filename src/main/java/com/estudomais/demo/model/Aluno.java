package com.estudomais.demo.model;

import java.io.Serializable;
import java.util.List;

public class Aluno implements Serializable {
    private String nome;
    private String email;
    private String curso;
    private String periodo;
    private String faculdade;
    private List<String> disciplinasVinculadas;

    public Aluno(String nome, String email, String curso, String periodo, String faculdade, List<String> disciplinasVinculadas) {
        this.nome = nome;
        this.email = email;
        this.curso = curso;
        this.periodo = periodo;
        this.faculdade = faculdade;
        this.disciplinasVinculadas = disciplinasVinculadas;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public String getFaculdade() { return faculdade; }
    public void setFaculdade(String faculdade) { this.faculdade = faculdade; }

    public List<String> getDisciplinasVinculadas() { return disciplinasVinculadas; }
    public void setDisciplinasVinculadas(List<String> disciplinasVinculadas) {
        this.disciplinasVinculadas = disciplinasVinculadas;
    }
}
