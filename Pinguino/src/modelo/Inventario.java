package modelo;

public class Inventario {

	int dadoRapido;
	int dadoLentos;
	int peces;
	int bolasNieve;

	public Inventario() {
		this.dadoRapido = 1;
		this.dadoLentos = 1;
		this.peces = 1;
		this.bolasNieve = 3;
	}

	public int getDadoRapido() {
		return dadoRapido;
	}

	public void setDadoLentos(int dadoLentos) {
		this.dadoLentos = dadoLentos;
	}

	public int getDadoLentos() {
		return dadoLentos;
	}

	public void setDadoRapido(int dadoRapido) {
		this.dadoRapido = dadoRapido;
	}

	public int getPeces() {
		return peces;
	}

	public void setPeces(int peces) {
		this.peces = peces;
	}

	public int getBolasNieve() {
		return bolasNieve;
	}

	public void setBolasNieve(int bolasNieve) {
		this.bolasNieve = bolasNieve;
	}

}
