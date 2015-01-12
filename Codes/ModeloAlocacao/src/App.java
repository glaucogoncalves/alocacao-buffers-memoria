import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class App {

	public static void main(String[] args) {
		ArrayList<String> arquivoLinhas = new ArrayList<String>();  		
		Path arquivo = Paths.get("entrada_buffer.txt");
		Path resultados = Paths.get("resultados.txt");
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
		} catch (IOException e) {
			System.out.println("ERRO AO LER O ARQUIVO DE ENTRADA!");
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			Modelo m = new Modelo();
			result = m.solveMe(tamanhoBuffer, taxaDeAcessoBuffer, qtdPortasBuffer,
					  capacidadeMemoria, larguraBandaMemoria, qtdPortasMemoria,
					  numBuffers, numMemorias);
			System.out.println("############################# FIM DO PROBLEMA " + (i + 1) + 
					   		   " #############################");
			
			String catDesp = categorizarEdesperdicio(tamanhoBuffer, taxaDeAcessoBuffer,
					qtdPortasBuffer, capacidadeMemoria, larguraBandaMemoria, qtdPortasMemoria);
			//arquivo estruturado: indice, categoria, desperdicio, frequencia, alocou
			resultadoLinha.add(i + ":" + catDesp + ":" + String.format( "%.2f", m.getFrequencia()) + ":" + result + 
							   String.format( "%.3f", m.getTime()));

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
	public static String categorizarEdesperdicio(int[] tamanhoBuffer, int[] taxaDeAcessoBuffer, int[] qtdPortasBuffer,
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
		
		double desperdicioCapMemoria = (double) 1-cap; //porcentagem de desperdicio de capacidade de memoria
		desperdicioCapMemoria = desperdicioCapMemoria*100;
		String retorno = categoria + ":" + String.format( "%.2f", desperdicioCapMemoria);
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
