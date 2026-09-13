package monokai.model;

public enum Situacao {
    ATIVO(1),
    INATIVO(0);

    private final int valor;

    Situacao(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
