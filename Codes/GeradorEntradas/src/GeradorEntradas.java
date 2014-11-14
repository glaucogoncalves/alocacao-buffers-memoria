/*
 * GeradorEntradas.java
 *
 * Gera as entradas que representam os buffers que serao utilizadas pelos algoritmos de alocacao em bancos de memoria.
 * Os arquivos gerados estao organizados da seguinte forma:
 * 
 * 2			 // numero de problemas que virao no arquivo
 * 2:2			 // numero de buffers do proximo problema e numero de memorias separados por ':'
 * 400000:2:100 // descricao dos 3 parametros do buffer separados por ':' <tamanho(KB)>:<numero_de_portas>:<taxa_de_acesso(KB/s)>
 * 250000:1:300
 * 800000:1:6500 // descricao dos 3 parametros do banco de memoria separados por ':' <capacidade(KB)>:<numero_de_portas>:<largura_de_banda(KB/s)>
 * 800000:1:6500
 * 3:2
 * 3550000:2:200
 * 2345000:1:500
 * 125000:2:100
 * 800000:16:500
 * 800000:16:500
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class GeradorEntradas {
	
	// caracteristicas possiveis para os bancos de memoria
	private static final int MIN_NBM = 2; 			// menor numero de bancos de memoria que sera considerado.
	private static final int MAX_NBM = 6; 			// maior numero de bancos de memoria que sera considerado.
	private static final int MIN_CM = 18; 			// menor capacidade de memoria que sera considerada em KB (256MB). 
			 								        // utilizar expoente da base por exemplo 0 = 2 ^ 0 = 1 KB; 4 = 2 ^ 4 = 16 KB; etc...
	private static final int MAX_CM = 23; 			// maior capacidade da memoria que sera considerada em KB (8GB).
	private static final int NUM_MT = 2;			// numero de tipos de memoria que serao utilizados. eg. DDR2-800 e DDR3-1600.
	// menor largura de banda da memoria que sera considerada em KB/s (6400 MB/s - DDR2-800).
	// maior largura de banda da memoria que sera considerada em KB/s (12800 MB/s - DDR3-1600).
	private static final int[] LB_TIPO = {6553600,13107200};
	private static final int MIN_NP = 15; 			// menor numero de portas que sera considerado.
	private static final int MAX_NP = 15; 			// maior numero de portas que sera considerado.
	private static final int NUM_PROB_DEF = 100;
	
	// caracteristicas possiveis para os buffers
	private static final int MIN_NB = 5; 			// menor numero de buffers que sera considerado.
	private static final int MAX_NB = 50; 			// maior numero de buffers de memoria que sera considerado.
	
	private static final int MAX_TB = 1048576; 		// maior tamanho de buffer possivel (KB). os menores serao fracao dele.
	private static final int NUM_TB = 4; 			// quantidade de capacidades diferentes de buffers

	private static final int MAX_TA = 16000; 		// maior taxa de acesso que o buffer podera ter a 1MHZ (KB/s). as menores taxas serao fracao desta.
	private static final int NUM_TA = 5; 			// quantidade de taxas de acesso diferentes diferentes de buffers
	

	private static final int MIN_NPB = 1; 			// menor numero de buffers que sera considerado.
	private static final int MAX_NPB = 2; 			// maior numero de buffers de memoria que sera considerado.

	// vetores de possibilidades de valores para os buffers 
	int ptb[] = new int[NUM_TB]; 			// vetor de possibilidade de valor para tamanho do buffer
	int pnpb[] = new int[MAX_NPB-MIN_NPB+1];			// vetor de possibilidade de valor para numero de portas do buffer
	int pta[] = new int[NUM_TA];					// vetor de possibilidade de valor para taxa de acesso
	
	// vetores de possibilidades de valores para as memorias
	int pcm[] = new int[MAX_CM-MIN_CM+1]; 			// vetor de possibilidade de valor para capacidade de memoria
	int pnpm[] = new int[MAX_NP-MIN_NP+1];			// vetor de possibilidade de valor para numero de portas maximo
	int plb[] = new int[NUM_MT];					// vetor de possibilidade de valor para largura de banda
	
	private ArrayList<String> arquivoLinhas = new ArrayList<String>();  
	
	private Path arquivo = Paths.get("entrada_buffer.txt");
	
	
	public static void main(String args[]) {		
		int numProblemas = 0; 	// especifica o numero de problemas que serao gerados no arquivo
		int numBuffers = 0;
		int numMemorias = 0;
		int somaTamanho = 0;
		int somaPortas = 0;
		int somaLarguraBanda = 0;

		GeradorEntradas gerador = new GeradorEntradas();
		gerador.carregaVetoresPossibilidades();
		// captura o numero de problemas da entrada do usuario
		if (args.length > 0) {
			numProblemas = Integer.parseInt(args[0]);
		}
		// utiliza o valor padrao para o numero de problemas que serao gerados
		else {
			numProblemas = NUM_PROB_DEF;
		}
		// escreve no buffer do arquivo o numero de problemas
		gerador.arquivoLinhas.add(numProblemas + "");
		// laco para geracao de todos os problemas
		for (int i = 0; i < numProblemas; i++) {
			// gera um numero aleatorio de numero de buffers
			numBuffers = GeradorEntradas.randInt(MIN_NB, MAX_NB);
			// gera um numero aleatorio de numero de memorias
			numMemorias = GeradorEntradas.randInt(MIN_NBM, MAX_NBM);
			// escreve no buffer do arquivo o numero de problemas 
			gerador.arquivoLinhas.add(numBuffers + ":" + numMemorias);
			for (int j = 0; j < numBuffers; j++) {
				gerador.arquivoLinhas.add(gerador.ptb[GeradorEntradas.randInt(0, (gerador.ptb.length-1))] + ":" +
										  gerador.pnpb[GeradorEntradas.randInt(0, (gerador.pnpb.length-1))] + ":" +
										  gerador.pta[GeradorEntradas.randInt(0, (gerador.pta.length-1))]);
			}
			for (int j = 0; j < numMemorias; j++) {
				gerador.arquivoLinhas.add(gerador.pcm[GeradorEntradas.randInt(0, (gerador.pcm.length-1))] + ":" +
						  				  gerador.pnpm[GeradorEntradas.randInt(0, (gerador.pnpm.length-1))] + ":" +
						  				  gerador.plb[GeradorEntradas.randInt(0, (gerador.plb.length-1))]);
			}
		}
		try {
			Files.write(gerador.arquivo, gerador.arquivoLinhas, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public void carregaVetoresPossibilidades() {
		// carrega vetores de possibilidades de valores para os buffers
		for (int i = 0; i < ptb.length; i++) {
			if(i == 0) {
				ptb[i] = MAX_TB; 			// vetor de possibilidade de valor para tamanho do buffer
			}
			else if(i == 1) {
				ptb[i] = MAX_TB/2; 			// vetor de possibilidade de valor para tamanho do buffer	
			}
			else {
				ptb[i] = (int)((double) MAX_TB/(Math.pow(2.0, Math.pow(2.0, (double) i))));  // vetor de possibilidade de valor para tamanho do buffer
			}
		}
		for (int i = 0; i < pnpb.length; i++) {
			pnpb[i] = MIN_NPB + 1;						// vetor de possibilidade de valor para numero de portas do buffer	
		}
		for (int i = 0; i < pta.length; i++) {
			if(i == 0) {
				pta[i] = MAX_TA; 			// vetor de possibilidade de valor para taxa de acesso
			}
			else {
				pta[i] = (int)((double) MAX_TA/(Math.pow(2.0, Math.pow(2.0, (double) (i-1)))));  // vetor de possibilidade de valor para taxa de acesso	
			}	
		}
		
		// vetores de possibilidades de valores para as memorias
		for (int i = 0; i < pcm.length; i++) {
			pcm[i] = 1 << (MIN_CM + i); 				// vetor de possibilidade de valor para capacidade de memoria
		}
		for (int i = 0; i < pnpm.length; i++) {
			pnpm[i] = MIN_NP + i; 			// vetor de possibilidade de valor para capacidade de memoria
		}
		for (int i = 0; i < plb.length; i++) {
			plb[i] = LB_TIPO[i]; 			// vetor de possibilidade de valor para capacidade de memoria
		}
	}
}