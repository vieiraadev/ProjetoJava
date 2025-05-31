package com.estudomais.demo.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Tarefa implements Serializable {
    private String titulo;
    private String descricao;
    private LocalDate dataEntrega;
    private String disciplinaAssociada;
    private double peso;

    public Tarefa(String titulo, String descricao, LocalDate dataEntrega, String disciplinaAssociada, double peso) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataEntrega = dataEntrega;
        this.disciplinaAssociada = disciplinaAssociada;
        this.peso = peso;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDate dataEntrega) { this.dataEntrega = dataEntrega; }

    public String getDisciplinaAssociada() { return disciplinaAssociada; }
    public void setDisciplinaAssociada(String disciplinaAssociada) { this.disciplinaAssociada = disciplinaAssociada; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
}
