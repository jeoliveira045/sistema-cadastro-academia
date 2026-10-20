package monokai.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Situacao {
	ATIVO(1), INATIVO(2);

	private static final Map<Integer, Situacao> POR_CODIGO = Arrays.stream(values())
			.collect(Collectors.toUnmodifiableMap(Situacao::getCodigo, Function.identity()));

	private int codigo;

	Situacao(int codigo) {
		this.codigo = codigo;
	}

	public int getCodigo() {
		return this.codigo;
	}

	public static Situacao fromCodigo(int codigo) {
		Situacao s = POR_CODIGO.get(codigo);
		if (s == null) {
			throw new IllegalArgumentException("Codigo de Situação inválido: " + codigo);
		}
		return s;
	}

}
