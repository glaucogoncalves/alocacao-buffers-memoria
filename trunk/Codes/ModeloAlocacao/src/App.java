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
		String categoria = "";
		double disperdicio = 0;
		
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
			resultadoLinha.add(i + "," + categoria +  "," + disperdicio + "," + m.getFrequencia() + "," + result);

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

}
