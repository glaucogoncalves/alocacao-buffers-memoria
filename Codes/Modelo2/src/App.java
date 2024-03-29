import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class App {

	public static void main(String[] args) {
		ArrayList<String> arquivoLinhas = new ArrayList<String>();
		ArrayList<String> arquivoPeriodos = new ArrayList<String>(); 
		Path arquivo = Paths.get("entrada_buffer.txt");
		Path arquivoPNome = Paths.get("periodos_obtidos.txt");
		Path resultados = Paths.get("resultadosModeloII.txt");
		double[] listaDePeriodos = new double[100];
		int numProblemas = 0;
		int numMemorias = 0;
		int numBuffers = 0;
		String tempString = "";
		String[] parts;
		int infeasibleCnt = 0;
		boolean result;
		
		ArrayList<String> resultadoLinha = new ArrayList<String>(); 
		
		try {
			arquivoLinhas = (ArrayList<String>) Files.readAllLines(arquivo, StandardCharsets.UTF_8);
			arquivoPeriodos = (ArrayList<String>) Files.readAllLines(arquivoPNome, StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("ERRO AO LER O ARQUIVO DE ENTRADA!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for (int i = 0; i < 100; i++) //cria um array apartir dos periodos obtidos num arquivo
		{
			listaDePeriodos[i] = Double.parseDouble(arquivoPeriodos.remove(0));
		}
		
		numProblemas = Integer.parseInt(arquivoLinhas.remove(0));
		for (int i = 0; i < numProblemas; i++) {
			System.out.println("############################# PROCESSANDO PROBLEMA " + (i + 1) + 
							   " #############################");
			tempString = arquivoLinhas.remove(0);
			parts = tempString.split(":");
			numBuffers = Integer.parseInt(parts[0]);
			numMemorias = Integer.parseInt(parts[1]);
			System.out.println(tempString);
			int[] tamanhoBuffer = new int[numBuffers];
			int[] taxaDeAcessoBuffer = new int[numBuffers];
			int[] qtdPortasBuffer = new int[numBuffers];
			int[] capacidadeMemoria = new int[numMemorias];
			int[] larguraBandaMemoria = new int[numMemorias];
			int[] qtdPortasMemoria = new int[numMemorias];
			
			for (int j = 0; j < numBuffers; j++) {
				tempString = arquivoLinhas.remove(0);
				parts = tempString.split(":");
				tamanhoBuffer[j] = Integer.parseInt(parts[0]);
				qtdPortasBuffer[j] = Integer.parseInt(parts[1]);
				taxaDeAcessoBuffer[j] = Integer.parseInt(parts[2]);
			}
			for (int j = 0; j < numMemorias; j++) {
				tempString = arquivoLinhas.remove(0);
				parts = tempString.split(":");
				capacidadeMemoria[j] = Integer.parseInt(parts[0]);
				qtdPortasMemoria[j] = Integer.parseInt(parts[1]);
				larguraBandaMemoria[j] = Integer.parseInt(parts[2]);
			}
			
			ModeloII m = new ModeloII();
			result = m.solveMe(tamanhoBuffer, taxaDeAcessoBuffer, qtdPortasBuffer,
					  capacidadeMemoria, larguraBandaMemoria, qtdPortasMemoria,
					  numBuffers, numMemorias, listaDePeriodos[i]);
			System.out.println("############################# FIM DO PROBLEMA " + (i + 1) + 
					   		   " #############################");
			String cat = "";
			String desp = "";
			if(result)
			{
				cat = categorizar(tamanhoBuffer, taxaDeAcessoBuffer, qtdPortasBuffer, capacidadeMemoria,
					larguraBandaMemoria, qtdPortasMemoria);
				desp = getDesperdicioArrayList(m.getListaEscolhida(), tamanhoBuffer);
			}
			else
			{
				cat = categorizar(tamanhoBuffer, taxaDeAcessoBuffer, qtdPortasBuffer, capacidadeMemoria,
						larguraBandaMemoria, qtdPortasMemoria);
				desp = getDesperdicio(capacidadeMemoria, tamanhoBuffer);
			}
			//arquivo estruturado: indice, categoria, desperdicio, qtdMemoriasDisponiveis, qtdMemoriasUsadas, alocou, tempo
			resultadoLinha.add(i + ":" + cat + ":" + desp + ":" + numMemorias + ":" + m.getCountMemories() + ":" + result + 
							   ":" + String.format( "%.3f", m.getTime()));
			
			if(result == false) {
				infeasibleCnt++;
			}
		}
		System.out.println("total infeasible: " + infeasibleCnt);
		try {
			Files.write(resultados, resultadoLinha, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String categorizar(int[] tamanhoBuffer, int[] taxaDeAcessoBuffer, int[] qtdPortasBuffer,
			int[] capacidadeMemoria, int[] larguraBandaMemoria, int[] qtdPortasMemoria)
	{
		int tamanhoB = somaValores(tamanhoBuffer); 		//soma todos os tamanhos dos buffers
		int taxadeaB = somaValores(taxaDeAcessoBuffer); //soma todas as taxas dos buffers
		int qtdportB = somaValores(qtdPortasBuffer);	//soma todas as quantidades de portas dos buffers
		int tamanhoM = somaValores(capacidadeMemoria);  //soma todas as capacidades das memorias
		int taxadeaM = somaValores(larguraBandaMemoria);//soma todas as taxas das memorias
		int qtdportM = somaValores(qtdPortasMemoria);   //soma todas as quantidades de portas das memorias

		
		double cap = (double)tamanhoB/tamanhoM;	//proporcao das somas das capacidade de memoria e de buffers
		double tax = (double)taxadeaB/taxadeaM;	//proporcao das somas das taxas de memoria e de buffers
		double por = (double)qtdportB/qtdportM;	//proporcao das somas das qts de portas de memoria e de buffers
		
		double maiorValor = maiorValor(cap, tax, por);
		
		String categoria = getCategoria(maiorValor);  //classifica o problema pela maior proporcao
		
		String retorno = categoria;
		return retorno;
	}
	public static String getDesperdicioArrayList(ArrayList<Integer> capacidadeMemoria, int[] tamanhoBuffer)
	{
		String retorno = "";
		if(capacidadeMemoria.size()>0){
			int tamanhoB = somaValores(tamanhoBuffer); 		//soma todos os tamanhos dos buffers
			int tamanhoM = somaValoresArrayList(capacidadeMemoria);  //soma todas as capacidades das memorias
			//System.out.println(tamanhoM);
			double cap = (double)tamanhoB/tamanhoM;	//proporcao das somas das capacidade de memoria e de buffers
					
			double desperdicioCapMemoria = (double) 1-cap; //porcentagem de desperdicio de capacidade de memoria
			desperdicioCapMemoria = desperdicioCapMemoria*100;
			retorno = String.format( "%.2f", desperdicioCapMemoria);
		}
		else
		{
			retorno = "0.0";
		}
		return retorno;
	}
	public static String getDesperdicio(int[] capacidadeMemoria, int[] tamanhoBuffer)
	{
		int tamanhoB = somaValores(tamanhoBuffer); 		//soma todos os tamanhos dos buffers
		int tamanhoM = somaValores(capacidadeMemoria);  //soma todas as capacidades das memorias

		double cap = (double)tamanhoB/tamanhoM;	//proporcao das somas das capacidade de memoria e de buffers
				
		double desperdicioCapMemoria = (double) 1-cap; //porcentagem de desperdicio de capacidade de memoria
		desperdicioCapMemoria = desperdicioCapMemoria*100;
		String retorno = String.format( "%.2f", desperdicioCapMemoria);
		return retorno;
	}
	public static int somaValores(int[] a)
	{
	    int total = 0;
	    for(int i = 0; i < a.length; i++){
	      total += a[i];  
	    }
	    
	    return total;
	}
	public static int somaValoresArrayList(ArrayList<Integer> a)
	{
	    int total = 0;
	    for(int i = 0; i < a.size(); i++){
	      total += a.get(i);  
	    }
	    
	    return total;
	}
	
	public static int somaValoresIndice(int[] a,int indice)
	{
	    int total = 0;
	    for(int i = 0; i < indice; i++){
	      total += a[i];  
	    }
	    
	    return total;
	}
	public static double maiorValor(double a, double b, double c)
	{
		double max = 0.0;
		if(a>b)
		{
			max = a;
		}
		else
		{
			max = b;
		}
		if(c > max)
		{
			max = c;
		}
		return max;
	}
	public static String getCategoria(double valor)
	{
		String cat = "";
		if(valor <= 0.4)
		{
			cat = "f";
		}
		else if(valor > 0.4 && valor <= 0.8)
		{
			cat = "m";
		}
		else if(valor> 0.8)
		{
			cat = "d";
		}
		
		return cat;
	}

}
