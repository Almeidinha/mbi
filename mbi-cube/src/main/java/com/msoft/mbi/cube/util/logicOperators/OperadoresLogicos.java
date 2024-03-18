package com.msoft.mbi.cube.util.logicOperators;

import java.io.Serial;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class OperadoresLogicos {

	public static final String						MAIOR						= ">";
	public static final String						MAIORIGUAL					= ">=";
	public static final String						MENOR						= "<";
	public static final String						MENORIGUAL					= "<=";
	public static final String						IGUAL						= "=";
	public static final String						DIFERENTE					= "<>";
	public static final String						CONTEM						= "like";
	public static final String						NAOCONTEM					= "notlike";
	public static final String						INICIACOM					= "like%";
	public static final String						PRIMEIROS_N					= "primeiros(n)";
	public static final String						ULTIMOS_N					= "ultimos(n)";
	public static final String						ENTRE_EXCLUSIVO				= "between";
	public static final String						ENTRE_INCLUSIVO				= "periodo";
	public static final OperaTorContemTexto operadorContemTexto			= new OperaTorContemTexto();
	public static final OperaTorNaoContemTexto operadorNaoContemTexto		= new OperaTorNaoContemTexto();
	public static final OperaTorDiferenteDimensao operadorDiferenteDimensao	= new OperaTorDiferenteDimensao();
	public static final OperaTorDiferenteNumero operadorDiferenteNumero		= new OperaTorDiferenteNumero();
	public static final OperaTorEntreExclusivoData operadorEntreExclusivoData	= new OperaTorEntreExclusivoData();
	public static final OperaTorEntreInclusivoData operadorEntreInclusivoData	= new OperaTorEntreInclusivoData();
	public static final OperaTorEntreNumero operadorEntreNumero			= new OperaTorEntreNumero();
	public static final OperaTorIgualDimensao operadorIgualDimensao		= new OperaTorIgualDimensao();
	public static final OperaTorIgualNumero operadorIgualNumero			= new OperaTorIgualNumero();
	public static final OperaTorIniciaComTexto operadorIniciaComTexto		= new OperaTorIniciaComTexto();
	public static final OperaTorMaiorData operadorMaiorData			= new OperaTorMaiorData();
	public static final OperaTorMaiorIgualData operadorMaiorIgualData		= new OperaTorMaiorIgualData();
	public static final OperaTorMaiorIgualNumero operadorMaiorIgualNumero	= new OperaTorMaiorIgualNumero();
	public static final OperaTorMaiorNumero operadorMaiorNumero			= new OperaTorMaiorNumero();
	public static final OperaTorMenorData operadorMenorData			= new OperaTorMenorData();
	public static final OperaTorMenorIgualData operadorMenorIgualData		= new OperaTorMenorIgualData();
	public static final OperaTorMenorIgualNumero operadorMenorIgualNumero	= new OperaTorMenorIgualNumero();
	public static final OperaTorMenorNumero operadorMenorNumero			= new OperaTorMenorNumero();

	private static class OperaTorContemTexto extends OperaTorValorUnico<String> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(String valor1, String valor2) {
			return valor1.contains(valor2);
		}
	}

	private static class OperaTorNaoContemTexto extends OperaTorValorUnico<String> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(String valor1, String valor2) {
			return !valor1.contains(valor2);
		}
	}

	private static class OperaTorDiferenteDimensao extends OperaTorValorUnico {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Object valor1, Object valor2) {
			return !valor1.equals(valor2);
		}
	}

	private static class OperaTorDiferenteNumero extends OperaTorValorUnico<Number> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Number valor1, Number valor2) {
			return valor1.doubleValue() != valor2.doubleValue();
		}
	}

	private static class OperaTorEntreExclusivoData implements OperaTor<Date> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compare(Date valor1, List<Date> valuesToCompare) {
			Date valorIni = valuesToCompare.get(0);
			Date valorFim = valuesToCompare.get(1);
			return valor1.compareTo(valorIni) >= 0 && valor1.compareTo(valorFim) < 0;
		}
	}

	private static class OperaTorEntreInclusivoData implements OperaTor<Date> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compare(Date valor1, List<Date> valuesToCompare) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dt = sdf.format(valor1);

			Date data = Date.valueOf(dt);

			Date valorIni = valuesToCompare.get(0);
			Date valorFim = valuesToCompare.get(1);
			return data.compareTo(valorIni) >= 0 && data.compareTo(valorFim) <= 0;
		}
	}

	private static class OperaTorEntreNumero implements OperaTor<Number> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compare(Number valor1, List<Number> valuesToCompare) {
			double valorIni = valuesToCompare.get(0).doubleValue();
			double valorFim = valuesToCompare.get(1).doubleValue();
			return valor1.doubleValue() >= valorIni && valor1.doubleValue() <= valorFim;
		}
	}

	private static class OperaTorIgualDimensao extends OperaTorValorUnico {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Object valor1, Object valor2) {
			return valor1.equals(valor2);
		}
	}

	private static class OperaTorIgualNumero extends OperaTorValorUnico<Number> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Number valor1, Number valor2) {
			return valor1.doubleValue() == valor2.doubleValue();
		}
	}

	private static class OperaTorIniciaComTexto extends OperaTorValorUnico<String> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(String valor1, String valor2) {
			return valor1.startsWith(valor2);
		}
	}

	private static class OperaTorMaiorData extends OperaTorValorUnico<Date> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Date valor1, Date valor2) {
			return valor1.compareTo(valor2) > 0;
		}
	}

	private static class OperaTorMaiorIgualData extends OperaTorValorUnico<Date> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Date valor1, Date valor2) {
			return valor1.compareTo(valor2) >= 0;
		}
	}

	private static class OperaTorMaiorIgualNumero extends OperaTorValorUnico<Number> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Number valor1, Number valor2) {
			return valor1.doubleValue() >= valor2.doubleValue();
		}
	}

	private static class OperaTorMaiorNumero extends OperaTorValorUnico<Number> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Number valor1, Number valor2) {
			return valor1.doubleValue() > valor2.doubleValue();
		}
	}

	private static class OperaTorMenorData extends OperaTorValorUnico<Date> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Date valor1, Date valor2) {
			return valor1.compareTo(valor2) < 0;
		}
	}

	private static class OperaTorMenorIgualData extends OperaTorValorUnico<Date> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Date valor1, Date valor2) {
			return valor1.compareTo(valor2) <= 0;
		}
	}

	private static class OperaTorMenorIgualNumero extends OperaTorValorUnico<Number> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Number valor1, Number valor2) {
			return valor1.doubleValue() <= valor2.doubleValue();
		}
	}

	private static class OperaTorMenorNumero extends OperaTorValorUnico<Number> {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public boolean compara(Number valor1, Number valor2) {
			return valor1.doubleValue() < valor2.doubleValue();
		}
	}
}
