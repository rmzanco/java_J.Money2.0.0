package br.unicamp.ft.h198760_r205541;

public class Financiamento {
    //private String chave;
    private String date;
    private String nome_env;
    private String parcela;
    private String valor;
    private String tipo;

    public Financiamento(){
    }

    public Financiamento(String date, String nome_env, String parcela, String valor, String tipo) {

        this.date = date;
        this.nome_env = nome_env;
        this.parcela = parcela;
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNome_env() {
        return nome_env;
    }

    public void setNome_env(String nome_env) {
        this.nome_env = nome_env;
    }

    public String getParcela() { return parcela; }

    public void setParcela(String parcela) {
        this.parcela = parcela;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
